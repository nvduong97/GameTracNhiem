package database;

import Model.Question;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
public class DataFunc {

    Connection con = Connections.Newconnect();

    public ArrayList<User> getUserList() {

        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<User> uslist = new ArrayList<User>();
        try {
            String sql = "Select * from [User]";
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                User us = new User();
                us.setId(rs.getInt(1));
                us.setUserName(rs.getString(2));
                us.setPassWord(rs.getString(3));
                us.setOnline(rs.getInt(4));
                us.setPoint(rs.getFloat(5));
                uslist.add(us);
            }
        } catch (SQLException ex) {
        }
        return uslist;
    }

    public ArrayList<User> getUserRank() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<User> uslist = new ArrayList<User>();
        try {
            String sql = "SELECT * FROM [User]\n"
                    + "ORDER BY point DESC";
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                User us = new User();
                us.setId(rs.getInt(1));
                us.setUserName(rs.getString(2));
                us.setPassWord(rs.getString(3));
                us.setOnline(rs.getInt(4));
                us.setPoint(rs.getFloat(5));
                uslist.add(us);
            }
        } catch (SQLException ex) {
        }
        return uslist;
    }

    public ArrayList<Question> getQuestion() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Question> list = new ArrayList<Question>();

        try {
            String sql = "SELECT TOP 5 * FROM Question ORDER BY CHECKSUM(NEWID())";
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt(1));
                question.setQuestion(rs.getString(2));
                question.setAnswerA(rs.getString(3));
                question.setAnswerB(rs.getString(4));
                question.setAnswerC(rs.getString(5));
                question.setAnswerD(rs.getString(6));
                question.setCorrectAnswer(rs.getInt(7));
                list.add(question);
            }
        } catch (SQLException ex) {

        }
        return list;
    }

    public boolean updateUser(User user) throws SQLException {
        String sqlStatement
                = "update [User] "
                + "set status = ?"
                + " where userID = ?;";
        PreparedStatement updateQuery = con.prepareStatement(sqlStatement);
        updateQuery.setInt(1, user.getOnline());
        updateQuery.setInt(2, user.getId());
        updateQuery.execute();
        return true;
    }

    public void updateDiem(User user) {
        try {
            String sql = "UPDATE [User] SET point = ? WHERE userID = ?";
            PreparedStatement updateQuery = con.prepareStatement(sql);
            updateQuery.setFloat(1, user.getPoint());
            updateQuery.setInt(2, user.getId());
            updateQuery.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DataFunc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
