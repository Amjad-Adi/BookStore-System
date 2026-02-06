package com.example.database;

import javafx.scene.control.Alert;

import java.sql.*;

public class DBConnection {

    private static final String URL="jdbc:mysql://localhost:3306/Book_Store?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "SomeUser";
    private static final String PASSWORD = "SomePassword";
    public static String id = "1230800";
    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
        con.setAutoCommit(true);
        return con;
    }

    public static void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(title);
        a.setHeaderText(title);
        a.showAndWait();
    }
}




