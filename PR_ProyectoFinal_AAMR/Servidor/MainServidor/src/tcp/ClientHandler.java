package tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true)
        ) {
            out.println("Bienvenido al servidor TCP");

            String message;
            while ((message = in.readLine()) != null) {

                System.out.println("Mensaje recibido: " + message);

                if (message.equalsIgnoreCase("exit")) {
                    out.println("Conexión cerrada");
                    break;
                }

                // Respuesta básica
                out.println("Servidor recibió: " + message);
            }

            socket.close();
            System.out.println("Cliente desconectado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
