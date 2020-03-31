/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.Game;
import Model.KMessage;
import Model.Question;
import Model.User;
import database.DataFunc;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {

    //Khai báo luồng gửi nhận
    private Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public User user;
    public static Game game1;
    public static Game game2;

    Boolean execute = true;

    ClientHandler(Socket socket) throws IOException {
        // truyền vào luồng socket
        this.socket = socket;
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        execute = true;
    }
    // Nhận dữ liệu

    void ReceiveMessage(KMessage msg) throws IOException {

        switch (msg.getType()) {
            // Nếu type = 0: Nhận từ Form Login. check đăng nhập
            case 0: {
                User temp = (User) msg.getObject();// Lấy dữ liệu
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                for (User obj : lstUser) {
                    if (obj.getUserName().contains(temp.getUserName()) && obj.getPassWord().contains(temp.getPassWord())) {
                        user = obj;
                    }
                }
                if (user != null) {
                    User objU = user;
                    objU.setOnline(1);
                    try {
                        Boolean a = df.updateUser(objU);
                    } catch (SQLException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (user != null) {
                        System.out.println("Server: Xin chao " + user.getUserName());
                    }
                }
                SendMessage(0, user);
                break;
            }
            // Nhận dữ liệu mời người tham gia trò chơi
            case 20: {
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                User objUser = new User();
                //objUser = lstUser.get(Integer.parseInt(msg.getObject().toString()));
                String a = msg.getObject().toString();
                for (User item : lstUser) {
                    if (item.getUserName().contains(a)) {
                        Main.userRoom2 = item;// Lưu tạp người tham gia 1
                        objUser = item;// Lấy thông tin người tham gia 1
                    }
                }
                Main.userRoom = this.user;// Lưu dữ liệu người vừa gửi yêu cầu lên
                for (ClientHandler lstUser1 : Main.lstClient) {
                    if (lstUser1.user.getUserName().contains(objUser.getUserName())) {
                        lstUser1.SendMessage(30, null);// Gửi yêu cầu tham gia trò chơi đến người kia
                    }
                }
                break;
            }
            // Nhận dữ liệu từ danh sách
            case 21: {
                // Lấy thông tin người dùng online
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                ArrayList<User> lstUserOnline = new ArrayList<User>();
                for (User lstUser1 : lstUser) {
                    if (lstUser1.getOnline() == 1 && !lstUser1.getUserName().contains(this.user.getUserName())) {
                        lstUserOnline.add(lstUser1);
                    }
                }
                SendMessage(21, lstUserOnline);// gửi trả lst những người dùng online
                break;
            }
            // Thông báo người kia đã vào phòng
            case 31: {
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                User objUser = new User();
                for (ClientHandler lstUser1 : Main.lstClient) {
                    if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName())) { // lấy luồng của người gửi yêu cầu vào phòng
                        lstUser1.SendMessage(33, null); // gửi thông báo xác nhận
                    }
                }
                break;
            }
            // Thông báo từ chối
            case 32: {
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                User objUser = new User();
                for (ClientHandler lstUser1 : Main.lstClient) {
                    if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName())) {
                        lstUser1.SendMessage(34, null);// Gửi thông báo từ chối
                    }
                }
                break;
            }
            // Yêu cầu thông tin sẵn sàng chơi
            case 40: {
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                User objUser = new User();
                for (ClientHandler lstUser1 : Main.lstClient) {
                    if (lstUser1.user.getUserName().contains(Main.userRoom2.getUserName()) && lstUser1 != this) {
                        lstUser1.SendMessage(41, null); // Gửi yêu cầu đến người còn lại
                    } else if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName()) && lstUser1 != this) {
                        lstUser1.SendMessage(41, null);// Gửi yêu cầu đến người còn lại
                    }
                }
                break;
            }
            // Gửi chấp nhận
            case 44: {
                DataFunc df = new DataFunc();
                ArrayList<Question> questions = new ArrayList<Question>();
                questions = df.getQuestion();
                KMessage temp = new KMessage();
                temp.setType(46);
                temp.setQuestions(questions);
                // Tạo câu hỏi và gửi đến cho các máy
                for (ClientHandler lstUser1 : Main.lstClient) {
                    lstUser1.SendMessage(temp);
                }
                break;
            }
            // Gửi từ chối
            case 45: {
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                User objUser = new User();
                for (ClientHandler lstUser1 : Main.lstClient) {
                    if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName())) {
                        lstUser1.SendMessage(47, null);// Thông báo từ chối
                    }
                }
                break;
            }
            // Gửi kết quả
            case 50: {
                DataFunc df = new DataFunc();
                if (game1 == null) {
                    game1 = (Game) msg.getObject();
                    if (game1.getCount() == 5) {
                        Main.Diem = 1;// Gửi lên đầu tiên, đúng 5 câu thì chiên thắng
                        game1.getUser().setPoint((game1.getUser().getPoint() + 1));
                        df.updateDiem(game1.getUser());
                        SendMessage(100, null);
                    } else {
                        //thông báo chờ
                        SendMessage(100, null);
                    }
                } else { // Đã có người gửi lên
                    game2 = (Game) msg.getObject();
                    if (Main.Diem == 1) {
                        Main.Diem = 0;
                        SendMessage(62, game1);//Thua
                        for (ClientHandler lstUser1 : Main.lstClient) {
                            if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName()) && !this.user.getUserName().contains(Main.userRoom.getUserName())) {
                                lstUser1.SendMessage(61, game2);
                            } else if (lstUser1.user.getUserName().contains(Main.userRoom2.getUserName()) && !this.user.getUserName().contains(Main.userRoom2.getUserName())) {
                                lstUser1.SendMessage(61, game2);
                            }
                        }
                    } else {
                        if (game2.getCount() == 5) {
                            Main.Diem = 1;
                            //thắng
                            game2.getUser().setPoint((game2.getUser().getPoint() + 1));
                            df.updateDiem(game2.getUser());
                            SendMessage(61, game1);
                            for (ClientHandler lstUser1 : Main.lstClient) {
                                if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName()) && !this.user.getUserName().contains(Main.userRoom.getUserName())) {
                                    lstUser1.SendMessage(62, game2);
                                } else if (lstUser1.user.getUserName().contains(Main.userRoom2.getUserName()) && !this.user.getUserName().contains(Main.userRoom2.getUserName())) {
                                    lstUser1.SendMessage(62, game2);
                                }
                            }
                        } else {
                            //hòa
                            Main.Diem = (float) 0.5;
                            SendMessage(60, game1);// Gửi thông tin về client
                            float d = (float) (game1.getUser().getPoint() + 0.5); // up điểm lên csdl
                            game1.getUser().setPoint(d);
                            df.updateDiem(game1.getUser());
                            d = (float) (game2.getUser().getPoint() + 0.5);
                            game2.getUser().setPoint(d);
                            df.updateDiem(game2.getUser());

                            for (ClientHandler lstUser1 : Main.lstClient) {
                                if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName()) && !this.user.getUserName().contains(Main.userRoom.getUserName())) {
                                    lstUser1.SendMessage(60, game2);
                                } else if (lstUser1.user.getUserName().contains(Main.userRoom2.getUserName()) && !this.user.getUserName().contains(Main.userRoom2.getUserName())) {
                                    lstUser1.SendMessage(60, game2);
                                }
                            }
                        }
                    }
                }
                break;
            }

            case 70: {
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserList();
                User objUser = new User();
                for (ClientHandler lstUser1 : Main.lstClient) {
                    if (lstUser1.user.getUserName().contains(Main.userRoom.getUserName()) && lstUser1 != this) {
                        lstUser1.SendMessage(71, null);
                    } else if (lstUser1.user.getUserName().contains(Main.userRoom2.getUserName()) && lstUser1 != this) {
                        lstUser1.SendMessage(71, null);
                    }
                }
                break;
            }
            //bảng xếp hạng
            case 80: {
                DataFunc df = new DataFunc();
                ArrayList<User> lstUser = new ArrayList<User>();
                lstUser = df.getUserRank();
                SendMessage(80, lstUser);// trả về lst người chơi và lst điểm
                break;
            }
        }
    }
    // Các hàm gửi tin với các đối số khác nhau

    public void SendMessage(int ty, Object obj) throws IOException {
        KMessage temp = new KMessage(ty, obj);
        SendMessage(temp);
    }

    public void SendMessage(int ty, ArrayList<User> obj) throws IOException {
        KMessage temp = new KMessage(ty, obj);
        SendMessage(temp);
    }

    public void SendMessage(int ty, ArrayList<User> obj, ArrayList<Question> questions) throws IOException {
        KMessage temp = new KMessage(ty, obj, questions);
        SendMessage(temp);
    }

    public void SendMessage(KMessage msg) throws IOException {
        outputStream.reset();
        outputStream.writeObject(msg);
    }

    public Boolean closeClient() throws Throwable {
        DataFunc df = new DataFunc();
        User objU = this.user;
        objU.setOnline(0);
        try {
            Boolean a = df.updateUser(objU);
        } catch (Exception e) {
        }
        Main.lstClient.remove(this);
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Client Exit");
        execute = false;

        return true;
    }

    @Override
    public void run() {
        while (execute) {
            try {
                Object o = inputStream.readObject();
                if (o != null) {
                    ReceiveMessage((KMessage) o);
                }

            } catch (IOException e) {
                try {
                    closeClient();
                } catch (Throwable ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
