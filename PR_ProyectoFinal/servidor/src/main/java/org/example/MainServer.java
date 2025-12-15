package org.example;

import org.example.tcp.TCPServer;
import org.example.udp.UDPServer;
import org.example.rest.RestAPI;
import org.example.rest.WebController;
import org.example.ws.ClienteWebSocket;
import org.example.rmi.UsuarioService;
import org.example.rmi.UsuarioServiceImpl;

import static spark.Spark.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServer {

    public static void main(String[] args) {

        System.out.println("Iniciando servidor...");

        // ================== RMI ==================
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            UsuarioService servicio = new UsuarioServiceImpl();
            registry.rebind("UsuarioService", servicio);
            System.out.println("Servicio RMI activo en puerto 1099");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ================== TCP ==================
        TCPServer servidorTCP = new TCPServer(5000);
        new Thread(servidorTCP::start).start();
        System.out.println("Servidor TCP escuchando en puerto 5000");

        // ================== UDP ==================
        UDPServer udpServer = new UDPServer(6000);
        new Thread(udpServer).start();
        System.out.println("Servidor UDP enviando notificaciones en puerto 6000");

        // ================== SPARK ==================
        port(8080);

     
        webSocket("/ws", ClienteWebSocket.class);

     
        staticFiles.location("/public");

      
        RestAPI.start();
        WebController.start();

       
        init();

        System.out.println("Web Admin disponible en http://localhost:8080");
        System.out.println("Cliente Web disponible en http://localhost:8080/cliente.html");
        System.out.println("WebSocket activo en ws://localhost:8080/ws");
    }
}
