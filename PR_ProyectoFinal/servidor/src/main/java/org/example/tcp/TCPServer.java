//areari
//aqui aplicamos lo de tcp pero con un servidor
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

                //aqui es donde se va manejando el multi hilo
                ClientHandler handler = new ClientHandler(cliente);
                new Thread(handler).start(); 
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
