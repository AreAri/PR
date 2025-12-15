package org.example.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private int puerto;

    public TCPServer(int puerto) {
        this.puerto = puerto;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {

            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());

                ClientHandler handler = new ClientHandler(cliente);
                new Thread(handler).start(); // MULTITHREADING
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
