//areari
//para poder conectar la bd
package org.example.db;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/pr_proyecto";
    private static final String USER = "postgres";     
    private static final String PASS = "1316";     

   public static Connection getConnection() {
    try {
        System.out.println("Intentando conectar a PostgreSQL...");
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("Conexión a PostgreSQL exitosa");
        return conn;
    } catch (Exception e) {
        System.out.println("ERROR conectando a PostgreSQL");
        e.printStackTrace();
        return null;
    }
}

}
