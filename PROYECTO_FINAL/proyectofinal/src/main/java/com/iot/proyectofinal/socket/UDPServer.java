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
    
    @Value("${app.socket.udp.port:8882}")  
    private int udpPort;
    
    private DatagramSocket socket;
    private volatile boolean running = false;
    
    @PostConstruct
    public void iniciar() {
        System.out.println("Iniciando servidor UDP en puerto " + udpPort + "...");
        
        try {
            socket = new DatagramSocket(udpPort);
            running = true;
            
            Thread serverThread = new Thread(this::escucharMensajes);
            serverThread.setDaemon(true);  // Para que no bloquee el cierre
            serverThread.start();
            
            System.out.println("Servidor UDP iniciado en puerto " + udpPort);
            
        } catch (SocketException e) {
            System.err.println("Error al iniciar servidor UDP en puerto " + udpPort + ": " + e.getMessage());
            System.err.println("   Probando puerto alternativo 8883...");
            
            // Intentar con puerto alternativo
            try {
                udpPort = 8883;
                socket = new DatagramSocket(udpPort);
                running = true;
                
                Thread altThread = new Thread(this::escucharMensajes);
                altThread.setDaemon(true);
                altThread.start();
                
                System.out.println("Servidor UDP iniciado en puerto alternativo " + udpPort);
            } catch (SocketException ex) {
                System.err.println("No se pudo iniciar servidor UDP en ningún puerto");
            }
        }
    }
    
    private void escucharMensajes() {
        byte[] buffer = new byte[1024];
        
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                String mensaje = new String(packet.getData(), 0, packet.getLength());
                String clientIp = packet.getAddress().getHostAddress();
                
                System.out.println("Mensaje UDP de " + clientIp + " -> " + mensaje);
                
                // Responder al cliente
                String respuesta = "UDP recibido: " + mensaje;
                byte[] respBytes = respuesta.getBytes();
                DatagramPacket respPacket = new DatagramPacket(
                    respBytes, respBytes.length, 
                    packet.getAddress(), packet.getPort()
                );
                socket.send(respPacket);
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error recibiendo mensaje UDP: " + e.getMessage());
                }
            }
        }
    }
    
    // Método para enviar mensaje UDP (para pruebas)
    public void enviarMensaje(String ip, int puerto, String mensaje) throws IOException {
        if (socket != null && !socket.isClosed()) {
            byte[] data = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(
                data, data.length, 
                java.net.InetAddress.getByName(ip), puerto
            );
            socket.send(packet);
        }
    }
    
    public int getPuerto() {
        return udpPort;
    }
    
    public boolean estaCorriendo() {
        return running && socket != null && !socket.isClosed();
    }
    
    @PreDestroy
    public void detener() {
        System.out.println("Deteniendo servidor UDP...");
        running = false;
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        System.out.println("Servidor UDP detenido");
    }
}
