//areari
//como tal aqui se aplica UDP para enviar mensajes a un cliente 
//igual se vio en clase por lo que en efecto es reciclado
package org.example.udp;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer implements Runnable {

    private static DatagramSocket socket;
    private static InetAddress address;
    private static int puerto;

    public UDPServer(int puerto) {
        try {
            UDPServer.puerto = puerto;
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enviarMensaje(String mensaje) {

        if (socket == null) {
            System.out.println("[UDP ERROR] Socket no inicializado");
            return;
        }

        try {
            byte[] buffer = mensaje.getBytes();
            DatagramPacket packet =
                new DatagramPacket(buffer, buffer.length, address, puerto);

            socket.send(packet);

            //con esto todo se dentro de la web
            org.example.ws.ClienteWebSocket.notificarUDP(mensaje);

            System.out.println("[UDP ENVIADO] " + mensaje);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // 
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
