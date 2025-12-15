package org.example.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.example.rmi.UsuarioService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ClienteWebSocket {

    private static Map<Session, String> clientes = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("[WS] Cliente conectado, esperando LOGIN...");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String mensaje) {

        // LOGIN:usuario:password
        if (mensaje.startsWith("LOGIN:")) {

            String[] partes = mensaje.split(":");
            if (partes.length != 3) return;

            String nombre = partes[1].trim();
            String password = partes[2].trim();

            // Usuario ya conectado
            if (clientes.containsValue(nombre)) {
                try {
                    session.getRemote().sendString(
                        "Usuario ya conectado"
                    );
                    session.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            // Hash de contraseña
            String hash = hashSHA256(password);

            // ---------- RMI ----------
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                UsuarioService service =
                        (UsuarioService) registry.lookup("UsuarioService");

                service.registrarUsuario(nombre, hash);

            } catch (Exception e) {
                e.printStackTrace();
            }

            clientes.put(session, nombre);
            enviarATodos("ONLINE " + nombre);
            enviarListaClientes();
            return;
        }

        // Mensaje normal
        String nombre = clientes.get(session);
        if (nombre != null) {
            enviarATodos(nombre + ": " + mensaje);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        String nombre = clientes.remove(session);
        if (nombre != null) {
            enviarATodos("OFFLINE " + nombre);
            enviarListaClientes();
        }
    }

    // ================= UTIL =================

    private void enviarATodos(String msg) {
        for (Session s : clientes.keySet()) {
            try {
                s.getRemote().sendString(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void enviarListaClientes() {
        StringBuilder sb = new StringBuilder("LISTA_CLIENTES:");
        for (String n : clientes.values()) {
            sb.append(n).append(",");
        }
        enviarATodos(sb.toString());
    }

    // SHA-256
    private String hashSHA256(String texto) {
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
