package com.iot.proyectofinal.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TCPServer {
    
    @Value("${app.socket.tcp.port:8881}")  
    private int tcpPort;
    
    @Value("${app.socket.max.connections:50}")
    private int maxConnections;
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = false;
    private AtomicInteger connectionCount = new AtomicInteger(0);
    private Map<String, Socket> activeClients = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void iniciar() {
        System.out.println("Iniciando servidor TCP en puerto " + tcpPort + "...");
        
        try {
            serverSocket = new ServerSocket(tcpPort);
            threadPool = Executors.newFixedThreadPool(maxConnections);
            running = true;
            
            Thread serverThread = new Thread(this::aceptarConexiones);
            serverThread.setDaemon(true);  // Para que no bloquee el cierre
            serverThread.start();
            
            System.out.println("Servidor TCP iniciado en puerto " + tcpPort);
            
        } catch (IOException e) {
            System.err.println("Error al iniciar servidor TCP en puerto " + tcpPort + ": " + e.getMessage());
            System.err.println("   Probando puerto alternativo 8884...");
            
            // Intentar con puerto alternativo
            try {
                tcpPort = 8884;
                serverSocket = new ServerSocket(tcpPort);
                threadPool = Executors.newFixedThreadPool(maxConnections);
                running = true;
                
                Thread altThread = new Thread(this::aceptarConexiones);
                altThread.setDaemon(true);
                altThread.start();
                
                System.out.println("Servidor TCP iniciado en puerto alternativo " + tcpPort);
            } catch (IOException ex) {
                System.err.println("No se pudo iniciar servidor TCP en ningún puerto");
            }
        }
    }
    
    private void aceptarConexiones() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientIp = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                String clientKey = clientIp + ":" + clientPort;
                
                System.out.println("Nueva conexión TCP desde: " + clientKey);
                connectionCount.incrementAndGet();
                activeClients.put(clientKey, clientSocket);
                
                threadPool.execute(() -> manejarCliente(clientSocket, clientKey));
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    private void manejarCliente(Socket clientSocket, String clientKey) {
        try (BufferedReader entrada = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter salida = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            salida.println("Bienvenido al servidor IoT - Proyecto Final PR");
            salida.println("Comandos: INFO, STATUS, TIME, ECHO <texto>, DISCONNECT");
            
            String mensaje;
            while ((mensaje = entrada.readLine()) != null && running) {
                System.out.println("Cliente " + clientKey + " dice: " + mensaje);
            
                String respuesta = procesarComando(mensaje, clientKey);
                salida.println(respuesta);
                
                if (mensaje.equalsIgnoreCase("DISCONNECT")) {
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error con cliente " + clientKey + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                activeClients.remove(clientKey);
                connectionCount.decrementAndGet();
                System.out.println("Cliente desconectado: " + clientKey);
            } catch (IOException e) {
                System.err.println("Error cerrando socket: " + e.getMessage());
            }
        }
    }
    
    private String procesarComando(String comando, String clientKey) {
        String upperComando = comando.toUpperCase();
        
        if (upperComando.equals("INFO")) {
            return "Servidor IoT - Proyecto Final PR\nCliente: " + clientKey;
        } else if (upperComando.equals("STATUS")) {
            return "Estado: OK\nConexiones activas: " + connectionCount.get() + 
                   "\nPuerto: " + tcpPort;
        } else if (upperComando.equals("TIME")) {
            return "Hora servidor: " + java.time.LocalTime.now() + 
                   "\nFecha: " + java.time.LocalDate.now();
        } else if (upperComando.startsWith("ECHO ")) {
            return "Eco: " + comando.substring(5);
        } else if (upperComando.equals("DISCONNECT")) {
            return "Desconectando... ¡Adiós!";
        } else {
            return "Comando no reconocido: " + comando + 
                   "\nComandos válidos: INFO, STATUS, TIME, ECHO <texto>, DISCONNECT";
        }
    }
    
    // Método broadcast mejorado
    public void broadcast(String mensaje) {
        System.out.println("Broadcast enviado: " + mensaje);
        
        activeClients.forEach((clientKey, socket) -> {
            if (socket != null && !socket.isClosed() && socket.isConnected()) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("[BROADCAST] " + mensaje);
                } catch (IOException e) {
                    System.err.println("Error en broadcast a " + clientKey + ": " + e.getMessage());
                }
            }
        });
    }
    
    // Métodos getter
    public int getPuerto() {
        return tcpPort;
    }
    
    public boolean estaCorriendo() {
        return running && serverSocket != null && !serverSocket.isClosed();
    }
    
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("running", running);
        stats.put("port", tcpPort);
        stats.put("activeConnections", connectionCount.get());
        stats.put("maxConnections", maxConnections);
        stats.put("connectedClients", new ArrayList<>(activeClients.keySet()));
        return stats;
    }
    
    public List<String> getConnectedClients() {
        return new ArrayList<>(activeClients.keySet());
    }
    
    @PreDestroy
    public void detener() {
        System.out.println("Deteniendo servidor TCP...");
        running = false;
        
        // Cerrar todas las conexiones de clientes
        activeClients.forEach((key, socket) -> {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error cerrando socket de cliente " + key);
            }
        });
        activeClients.clear();
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando server socket: " + e.getMessage());
        }
        
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Servidor TCP detenido");
    }
}
