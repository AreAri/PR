package TCP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in))
        ) {

            System.out.println(in.readLine());

            String userInput;
            while (true) {
                System.out.print("Cliente: ");
                userInput = console.readLine();

                out.println(userInput);

                String response = in.readLine();
                System.out.println("Servidor: " + response);

                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
