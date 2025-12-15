//areari
//esta aqui udp, multitrading y seguridad
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

    private static final Map<Session, String> clientes = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    //cuando el ciente se conecta 
    public void onConnect(Session session) {
        System.out.println("[WS] Cliente conectado");
    }

    @OnWebSocketMessage
    //cuando le llega un mensaje 
    public void onMessage(Session session, String mensaje) {
        System.out.println("[WS] Mensaje recibido: " + mensaje);

        //el registro, parte que causo problemas porque de la nada todo fallo 
        //aunque bueno como tal aqui es tanto login como registro puede ser una u otra
        if (mensaje.startsWith("LOGIN:") || mensaje.startsWith("REGISTER:")) {

            String[] partes = mensaje.split(":");
            if (partes.length != 3) {
                enviar(session, "Formato inválido");
                return;
            }

            String tipo = partes[0];
            String usuario = partes[1].trim();
            String password = partes[2].trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                enviar(session, "Usuario y contraseña obligatorios");
                return;
            }

            // Evitar usuarios duplicados conectados
            if (clientes.containsValue(usuario)) {
                enviar(session, "Usuario ya conectado");
                return;
            }

            //solo el admin, asi es este era uno de los problemas porque como lo estaba registradeo
            //como tal en la bd y solo lo aplico dentro del codigo ya que no puede haber otro
            if (usuario.equalsIgnoreCase("admin")) {
                if (password.equals("1234")) {
                    clientes.put(session, "ADMIN");
                    enviar(session, "LOGIN_OK");
                    enviarATodos("ONLINE ADMIN");
                    enviarListaClientes();
                    System.out.println("Admin autenticado");
                    return;
                } else {
                    enviar(session, "Usuario o contraseña incorrectos");
                    return;
                }
            }

            String hash = hashSHA256(password);

            try {
                //empezamos con el rmi con el puerto 
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                //y obtenemos la referencia del usuario 
                UsuarioService service = (UsuarioService) registry.lookup("UsuarioService");

                boolean ok;

                //si empezamos a con el registro 
                if (tipo.equals("REGISTER")) {
                    ok = service.registrar(usuario, hash); //le hablamos al metodo
                    if (!ok) { //si existe adios bbye no puedes registrate de nuez
                        enviar(session, "El usuario ya existe");
                        return;
                    }
                } else { // LOGIN
                    ok = service.autenticar(usuario, hash);
                    if (!ok) { //no es correcta la info
                        enviar(session, "Usuario o contraseña incorrectos");
                        return;
                    }
                }

                // AUTENTICACIÓN EXITOSA
                clientes.put(session, usuario);

                enviar(session, "LOGIN_OK");
                enviarATodos("ONLINE " + usuario);//o chicos alguien se conecto 
                enviarListaClientes();//actualizamos lista cada que se conecte alguien 

                System.out.println("Usuario autenticado: " + usuario);

            } catch (Exception e) {
                e.printStackTrace();
                enviar(session, "Error del servidor");
            }

            return;
        }

        // ---------------- MENSAJES DE CHAT ----------------
        String usuario = clientes.get(session);
        if (usuario != null) {
            enviarATodos(usuario + ": " + mensaje);
        }
    }

    @OnWebSocketClose
    //cerrar sesion
    public void onClose(Session session, int statusCode, String reason) {
        String usuario = clientes.remove(session);//quitamos todo
        if (usuario != null) {
            enviarATodos("OFFLINE " + usuario);//le decimos a todos quien se fue
            enviarListaClientes();
            System.out.println("Usuario desconectado: " + usuario);
        }
    }

//enviamos mensajes a una sesion mas especifica que vendrian siendo mas que nada de autentitcacion
//o errores
    private void enviar(Session s, String msg) {
        try {
            s.getRemote().sendString(msg);
        } catch (Exception e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        }
    }
//el mensaje va pa todos
    private void enviarATodos(String msg) {
        for (Session s : clientes.keySet()) {
            enviar(s, msg);
        }
    }

    private void enviarListaClientes() {
        StringBuilder sb = new StringBuilder("LISTA_CLIENTES:");
        for (String u : clientes.values()) {
            sb.append(u).append(",");
        }
        enviarATodos(sb.toString());
    }

    // udp
    //solo loa puede hacer el admi y es basicamente mandar un mensaje con lo que sea
    public static void notificarUDP(String mensaje) {
        for (Session s : clientes.keySet()) {
            try {
                s.getRemote().sendString("NOTI UDP: " + mensaje);
            } catch (Exception ignored) {}
        }
    }

    // ---------------- HASH ----------------
    //o bien ocultar contraseñas en la bd
    //este como tal si lo tome de lo que ya tenia pero con ayuda de la ia y otras investigaciones
    //se logo obtener algo a lo que si le entienda 
    private String hashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes("UTF-8"));//tamano cual es
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {//aqui empezamos co la conversion 
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
