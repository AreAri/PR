//AREARI
//autenticamos el tcp 
package org.example.tcp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {//empieza el hilo, esta vivo
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println("Bienvenido al servidor TCP");
            out.println("Ingrese usuario:");

            String usuario = in.readLine();
            out.println("Ingrese contraseña:");
            String password = in.readLine();

            if (usuario.equals("admin") && password.equals("1234")) {
                out.println("AUTENTICACION CORRECTA");
            } else {
                out.println("ERROR DE AUTENTICACION");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("Cliente desconectado");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
