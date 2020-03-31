package database;

import java.sql.Connection;
import java.sql.DriverManager;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
public class Connections {

    private static Connection getConnect() {
        Connection connection = null;
        try {
            final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            final String server = "jdbc:sqlserver://localhost:1433;databaseName = Game";
            final String user = "sa";
            final String pass = "1234";
            Class.forName(driver);
            connection = DriverManager.getConnection(server, user, pass);
            if (connection == null) {
                System.out.println("Ket noi khong thanh cong");
            } else {
                System.out.println("Ket noi thanh cong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection Newconnect() {
        return getConnect();
    }
}
