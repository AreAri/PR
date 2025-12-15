package com.iot.proyectofinal.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TCPServer {
    
    @Value("${app.tcp.port:8081}")
    private int tcpPort;
    
    @Value("${app.max.connections:10}")
    private int maxConnections;
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = false;
    
    // Inicializar servidor cuando Spring arranque
    @PostConstruct
    public void iniciar() {
        System.out.println("Iniciando servidor TCP en puerto " + tcpPort + "...");
        
        try {
            serverSocket = new ServerSocket(tcpPort);
            threadPool = Executors.newFixedThreadPool(maxConnections);
            running = true;
            
            // Iniciar hilo principal que acepta conexiones
            Thread serverThread = new Thread(this::aceptarConexiones);
            serverThread.start();
            
            System.out.println("Servidor TCP iniciado en puerto " + tcpPort);
        } catch (IOException e) {
            System.err.println("Error al iniciar servidor TCP: " + e.getMessage());
        }
    }
    
    // Metodo que acepta conexiones de clientes
    private void aceptarConexiones() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nueva conexion TCP desde: " + 
                    clientSocket.getInetAddress().getHostAddress());
                
                // Manejar cliente en un hilo separado
                threadPool.execute(() -> manejarCliente(clientSocket));
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error aceptando conexion: " + e.getMessage());
                }
            }
        }
    }
    
    // Metodo que maneja la comunicacion con un cliente
    private void manejarCliente(Socket clientSocket) {
        String clientIp = clientSocket.getInetAddress().getHostAddress();
        
        try (BufferedReader entrada = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter salida = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            System.out.println("Manejando cliente TCP: " + clientIp);
            
            // Enviar mensaje de bienvenida
            salida.println("Bienvenido al servidor IoT - Proyecto Final PR");
            salida.println("Comandos disponibles: INFO, STATUS, DISCONNECT");
            
            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Cliente " + clientIp + " dice: " + mensaje);
                
                // Procesar comando
                String respuesta = procesarComando(mensaje, clientIp);
                salida.println(respuesta);
                
                if (mensaje.equalsIgnoreCase("DISCONNECT")) {
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error con cliente " + clientIp + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Cliente desconectado: " + clientIp);
            } catch (IOException e) {
                System.err.println("Error cerrando socket: " + e.getMessage());
            }
        }
    }
    
    // Procesar comandos recibidos
    private String procesarComando(String comando, String clientIp) {
        switch (comando.toUpperCase()) {
            case "INFO":
                return "Servidor IoT - Proyecto Final PR - Cliente: " + clientIp;
            case "STATUS":
                return "Estado: OK - Conexiones activas: " + 
                    (threadPool != null ? "varias" : "0");
            case "DISCONNECT":
                return "Desconectando... Adios!";
            case "TIME":
                return "Hora del servidor: " + java.time.LocalTime.now();
            default:
                return "Comando no reconocido: " + comando + 
                    " - Comandos validos: INFO, STATUS, TIME, DISCONNECT";
        }
    }
    
    // Detener servidor cuando Spring se detenga
    @PreDestroy
    public void detener() {
        System.out.println("Deteniendo servidor TCP...");
        running = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando server socket: " + e.getMessage());
        }
        
        if (threadPool != null) {
            threadPool.shutdown();
        }
        
        System.out.println("Servidor TCP detenido");
    }
    
    // Metodo para verificar si el servidor esta corriendo
    public boolean estaCorriendo() {
        return running;
    }
    
    // Metodo para obtener el puerto
    public int getPuerto() {
        return tcpPort;
    }
}