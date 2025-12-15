package com.iot.proyectofinal.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

@Component
public class UDPServer {
    
    @Value("${app.udp.port:8082}")
    private int udpPort;
    
    private DatagramSocket socket;
    private volatile boolean running = false;
    private Thread serverThread;
    
    // Iniciar servidor UDP
    @PostConstruct
    public void iniciar() {
        System.out.println("Iniciando servidor UDP en puerto " + udpPort + "...");
        
        try {
            socket = new DatagramSocket(udpPort);
            running = true;
            
            serverThread = new Thread(this::escucharMensajes);
            serverThread.start();
            
            System.out.println("Servidor UDP iniciado en puerto " + udpPort);
        } catch (SocketException e) {
            System.err.println("Error al iniciar servidor UDP: " + e.getMessage());
        }
    }
    
    // Metodo que escucha mensajes UDP
    private void escucharMensajes() {
        byte[] buffer = new byte[1024];
        
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                String mensaje = new String(packet.getData(), 0, packet.getLength());
                String clientIp = packet.getAddress().getHostAddress();
                int clientPort = packet.getPort();
                
                System.out.println("Mensaje UDP de " + clientIp + ":" + clientPort + 
                    " -> " + mensaje);
                
                // Procesar mensaje
                String respuesta = procesarMensajeUDP(mensaje);
                
                // Enviar respuesta si es necesario
                if (respuesta != null && !respuesta.isEmpty()) {
                    byte[] respuestaBytes = respuesta.getBytes();
                    DatagramPacket respuestaPacket = new DatagramPacket(
                        respuestaBytes, respuestaBytes.length,
                        packet.getAddress(), packet.getPort()
                    );
                    socket.send(respuestaPacket);
                }
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error recibiendo mensaje UDP: " + e.getMessage());
                }
            }
        }
    }
    
    // Procesar mensajes UDP recibidos
    private String procesarMensajeUDP(String mensaje) {
        // Para notificaciones rapidas
        if (mensaje.startsWith("ALERTA:")) {
            System.out.println("ALERTA RECIBIDA: " + mensaje.substring(7));
            return "ALERTA_ACK";
        } else if (mensaje.startsWith("STATUS:")) {
            System.out.println("STATUS UPDATE: " + mensaje.substring(7));
            return "STATUS_OK";
        } else if (mensaje.equals("PING")) {
            return "PONG - Servidor UDP activo";
        } else {
            System.out.println("Mensaje UDP recibido: " + mensaje);
            return "RECIBIDO: " + mensaje;
        }
    }
    
    // Detener servidor UDP
    @PreDestroy
    public void detener() {
        System.out.println("Deteniendo servidor UDP...");
        running = false;
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        if (serverThread != null) {
            try {
                serverThread.join(2000); // Esperar a que termine
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Servidor UDP detenido");
    }
    
    // Enviar mensaje UDP (para broadcast o notificaciones)
    public void enviarMensaje(String ip, int puerto, String mensaje) {
        try (DatagramSocket tempSocket = new DatagramSocket()) {
            byte[] data = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(
                data, data.length,
                java.net.InetAddress.getByName(ip), puerto
            );
            tempSocket.send(packet);
            System.out.println("Mensaje UDP enviado a " + ip + ":" + puerto);
        } catch (IOException e) {
            System.err.println("Error enviando mensaje UDP: " + e.getMessage());
        }
    }
    
    // Verificar si esta corriendo
    public boolean estaCorriendo() {
        return running;
    }
    
    // Obtener puerto
    public int getPuerto() {
        return udpPort;
    }
}