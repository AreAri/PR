//areari
package org.example.rmi;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import org.example.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;

//heredaos las funciones del rmi y recibir llamadas con otros jvms
public class UsuarioServiceImpl extends UnicastRemoteObject
        implements UsuarioService {

    public UsuarioServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean registrar(String nombre, String passwordHash)
            throws RemoteException {

        if ("ADMIN".equalsIgnoreCase(nombre) || "admin".equalsIgnoreCase(nombre)) {
            return false; // No permitir registrar usuarios con nombre admin
        }

        try (Connection conn = DBConnection.getConnection()) {

            // Verificar si ya existe
            PreparedStatement check = conn.prepareStatement(
                "SELECT id FROM clientes WHERE nombre = ?"
            );
            check.setString(1, nombre);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                return false; // ya existe
            }

            // Insertar nuevo usuario
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO clientes(nombre, password) VALUES (?, ?)"
            );
            insert.setString(1, nombre);
            insert.setString(2, passwordHash);
            insert.executeUpdate();

            System.out.println("[RMI] Usuario registrado: " + nombre);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean autenticar(String nombre, String passwordHash)
            throws RemoteException {

        // para el admin es para mas que nada pider hacer que se permita el login y exista la
        //conexxion em ambos
        if ("admin".equalsIgnoreCase(nombre)) {
            // Hash SHA-256 de "1234" (contraseña del admin)
            String adminHash = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
            boolean esAdmin = adminHash.equals(passwordHash);
            
            if (esAdmin) {
                System.out.println("[RMI] Admin autenticado exitosamente");
            } else {
                System.out.println("[RMI] Fallo autenticación admin - hash incorrecto");
            }
            return esAdmin;
        }

        // ================= USUARIOS NORMALES =================
        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM clientes WHERE nombre = ? AND password = ?"
            );
            ps.setString(1, nombre);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();
            boolean autenticado = rs.next();
            
            if (autenticado) {
                System.out.println("[RMI] Usuario autenticado: " + nombre);
            } else {
                System.out.println("[RMI] Fallo autenticación: " + nombre);
            }
            
            return autenticado;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método opcional para crear el usuario admin en la base de datos al iniciar
    public static void crearAdminSiNoExiste() {
        try {
            // Hash de "1234"
            String adminHash = hashSHA256("1234");
            
            try (Connection conn = DBConnection.getConnection()) {
                
                // Verificar si admin existe
                PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM clientes WHERE nombre = 'admin'"
                );
                ResultSet rs = check.executeQuery();
                
                if (!rs.next()) {
                    // Crear admin
                    PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO clientes(nombre, password) VALUES ('admin', ?)"
                    );
                    insert.setString(1, adminHash);
                    insert.executeUpdate();
                    System.out.println("[RMI] Usuario admin creado en la base de datos");
                } else {
                    System.out.println("[RMI] Usuario admin ya existe en la base de datos");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método auxiliar para generar hash SHA-256
    private static String hashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
