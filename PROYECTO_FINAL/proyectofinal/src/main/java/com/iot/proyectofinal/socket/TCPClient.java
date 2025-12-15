package com.iot.proyectofinal.socket;

import java.io.*;
import java.net.Socket;

public class TCPClient {
    
    private String serverIp;
    private int serverPort;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    
    // Constructor
    public TCPClient(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }
    
    // Conectar al servidor
    public boolean conectar() {
        try {
            socket = new Socket(serverIp, serverPort);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            
            // Leer mensaje de bienvenida
            String respuesta = entrada.readLine();
            System.out.println("Servidor dice: " + respuesta);
            
            return true;
        } catch (IOException e) {
            System.err.println("Error conectando al servidor: " + e.getMessage());
            return false;
        }
    }
    
    // Enviar mensaje al servidor
    public String enviarMensaje(String mensaje) {
        try {
            if (salida != null) {
                salida.println(mensaje);
                
                // Leer respuesta
                StringBuilder respuesta = new StringBuilder();
                String linea;
                
                // Leer hasta encontrar linea vacia o timeout
                while (entrada.ready() && (linea = entrada.readLine()) != null) {
                    respuesta.append(linea).append("\n");
                }
                
                return respuesta.toString().trim();
            }
        } catch (IOException e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        }
        return "Error de comunicacion";
    }
    
    // Desconectar del servidor
    public void desconectar() {
        try {
            if (salida != null) {
                salida.println("DISCONNECT");
            }
            
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
            System.out.println("Desconectado del servidor");
        } catch (IOException e) {
            System.err.println("Error desconectando: " + e.getMessage());
        }
    }
    
    // Metodo de prueba
    public void pruebaComunicacion() {
        if (conectar()) {
            System.out.println("Prueba de comunicacion TCP:");
            
            String[] comandos = {"INFO", "STATUS", "TIME", "DISCONNECT"};
            
            for (String comando : comandos) {
                System.out.println("Enviando: " + comando);
                String respuesta = enviarMensaje(comando);
                System.out.println("Respuesta: " + respuesta);
                
                try {
                    Thread.sleep(1000); // Esperar 1 segundo entre comandos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            desconectar();
        }
    }
    
    // Metodo main para probar el cliente
    public static void main(String[] args) {
        TCPClient cliente = new TCPClient("localhost", 8081);
        cliente.pruebaComunicacion();
    }
}