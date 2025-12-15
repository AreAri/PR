package com.iot.proyectofinal.socket;

import java.io.*;
import java.net.Socket;

public class TCPClient {
    
    public static void pruebaTCP() {
        System.out.println("Probando conexión TCP al servidor...");
        
        try (Socket socket = new Socket("localhost", 8881);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {
            
            // Leer mensaje de bienvenida
            System.out.println("Servidor dice:");
            String linea;
            while ((linea = entrada.readLine()) != null && !linea.contains("----")) {
                System.out.println("  " + linea);
            }
            
            // Enviar comandos de prueba
            String[] comandos = {"INFO", "STATUS", "TIME", "ECHO Hola mundo", "DISCONNECT"};
            
            for (String comando : comandos) {
                System.out.println("\nEnviando: " + comando);
                salida.println(comando);
                
                String respuesta = entrada.readLine();
                System.out.println("Respuesta: " + respuesta);
                
                Thread.sleep(500);
            }
            
        } catch (Exception e) {
            System.err.println("Error en prueba TCP: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        pruebaTCP();
    }
}
