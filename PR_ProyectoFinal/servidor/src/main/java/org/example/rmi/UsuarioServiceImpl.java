package org.example.rmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import org.example.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioServiceImpl extends UnicastRemoteObject
        implements UsuarioService {

    public UsuarioServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean registrarUsuario(String nombre, String passwordHash)
            throws RemoteException {

        // Admin no se registra
        if ("ADMIN".equalsIgnoreCase(nombre)) return true;

        try (Connection conn = DBConnection.getConnection()) {


            // Verificar si ya existe
            PreparedStatement check = conn.prepareStatement(
                "SELECT id FROM clientes WHERE nombre = ?"
            );
            check.setString(1, nombre);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // Ya existe → NO se vuelve a registrar
                return true;
            }

            // Insertar nuevo usuario
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO clientes(nombre, password) VALUES (?, ?)"
            );
            insert.setString(1, nombre);
            insert.setString(2, passwordHash);
            insert.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
