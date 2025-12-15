//areari
//aqui va el codigo del cliente TCP, que como tal es como se ve en clase
package com.example.tcp;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    private String host;
    private int puerto;

    public TCPClient(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public void start() {
        try (
            Socket socket = new Socket(host, puerto);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
        ) {

            // Mensajes del servidor
            System.out.println(in.readLine()); // Bienvenida
            System.out.print(in.readLine() + " ");
            out.println(scanner.nextLine());

            System.out.print(in.readLine() + " ");
            out.println(scanner.nextLine());

            // Respuesta final
            System.out.println(in.readLine());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
