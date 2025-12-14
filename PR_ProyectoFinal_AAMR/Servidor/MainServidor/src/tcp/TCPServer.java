package tcp;

import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Servidor TCP iniciado en el puerto " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Hilo por cliente
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
