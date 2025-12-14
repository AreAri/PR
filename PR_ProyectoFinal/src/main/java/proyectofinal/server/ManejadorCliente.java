//AreAri
//hilo de los clientes para el servidor 
//evitamos asi poner todo en el servidor y no me confundo (igual si lo vimos en clase)
package proyectofinal.server;

import java.io.*;
import java.net.*;
import proyectofinal.model.Mensaje;
import com.google.gson.Gson;

public class ManejadorCliente implements Runnable {
    private Socket socketCliente;
    private String idCliente;
    private ServidorTCP servidor;
    private BufferedReader entrada;
    private PrintWriter salida;
    private Gson gson;
    
    //señor constructor
    public ManejadorCliente(Socket socket, String idCliente, ServidorTCP servidor) {
        this.socketCliente = socket;
        this.idCliente = idCliente;
        this.servidor = servidor;
        this.gson = new Gson();
    }
    
    //metodo para iniciar el hilo
    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(socketCliente.getOutputStream(), true);
            enviarMensaje("Bienvenido al servidor, Tu ID: " + idCliente);
            
            System.out.println("Comunicacion establecida con cliente: " + idCliente);
            
            String mensajeCliente;
            while ((mensajeCliente = entrada.readLine()) != null) {
                System.out.println("RECIBIDO de [" + idCliente + "]: " + mensajeCliente);
                
                procesarMensaje(mensajeCliente);
                
                String respuesta = "Servidor recibio: " + mensajeCliente;
                enviarMensaje(respuesta);
                
                // si pones exit o quit se desconecta asi que aguas
                if ("exit".equalsIgnoreCase(mensajeCliente) || "quit".equalsIgnoreCase(mensajeCliente)) {
                    enviarMensaje("Desconectando...");
                    break;
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error en comunicacion con " + idCliente + ": " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }
    
    private void procesarMensaje(String mensaje) {
        try {
    //pasamos el mensaje a json
            Mensaje msg = gson.fromJson(mensaje, Mensaje.class);
            
          //y vemos la info
            System.out.println("  Tipo de mensaje: " + msg.getTipo());
            System.out.println("  Remitente: " + msg.getRemitente());
            System.out.println("  Contenido: " + msg.getContenido());
            
        } catch (Exception e) {
            System.out.println("  Mensaje de texto simple: " + mensaje);
        }
    }

    //enviamos mensajes al cliente
    private void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
            System.out.println("ENVIADO a [" + idCliente + "]: " + mensaje);
        }
    }
    
    //y lo enviamos en json
    public void enviarMensaje(Mensaje mensaje) {
        if (salida != null) {
            String json = gson.toJson(mensaje);
            salida.println(json);
            System.out.println("ENVIADO (JSON) a [" + idCliente + "]: " + json);
        }
    }
    
    //adios cliente
    private void cerrarConexion() {
        try {
            // Cerrar streams
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            
            // Cerrar socket
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }
            
            // le decimos al server que lo quite porque ya no esta
            servidor.removerCliente(idCliente);
            
            System.out.println("Conexion cerrada con cliente: " + idCliente);
            
        } catch (IOException e) {
            System.err.println("Error cerrando conexion: " + e.getMessage());
        }
    }

    public String getIdCliente() {
        return idCliente;
    }
}