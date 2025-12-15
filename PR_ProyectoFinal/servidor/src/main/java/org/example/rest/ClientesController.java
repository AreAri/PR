package org.example.rest;

import org.example.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientesController {

    // ================= REGISTRO =================

    public static void agregarCliente(String nombre) {

        // NO registrar admin
        if (nombre == null || nombre.equalsIgnoreCase("ADMIN")) {
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            // Verificar si ya existe
            String checkSql = "SELECT 1 FROM clientes WHERE nombre = ?";
            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setString(1, nombre);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // Ya existe → no insertar
                System.out.println("[DB] Cliente ya registrado: " + nombre);
                return;
            }

            // nsertar solo si NO existe
            String insertSql = "INSERT INTO clientes(nombre) VALUES (?)";
            PreparedStatement insert = conn.prepareStatement(insertSql);
            insert.setString(1, nombre);
            insert.executeUpdate();

            System.out.println("[DB] Cliente registrado: " + nombre);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= HTML =================

    public static String getClientesHTML() {

        StringBuilder html = new StringBuilder();
        html.append("<h2>Clientes Registrados (PostgreSQL)</h2><ul>");

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT nombre FROM clientes ORDER BY nombre";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                html.append("<li>").append(rs.getString("nombre")).append("</li>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        html.append("</ul><a href='/admin'>Volver</a>");
        return html.toString();
    }
}
