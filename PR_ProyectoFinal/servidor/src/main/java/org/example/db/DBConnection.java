package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/pr_proyecto";
    private static final String USER = "postgres";      // cambia si usas otro
    private static final String PASS = "1316";      // cambia si usas otro

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
