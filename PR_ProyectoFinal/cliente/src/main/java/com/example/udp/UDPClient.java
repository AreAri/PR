//areari
//udp del cliente, vemos los mensajes UDP que mande wel admin
package com.example.udp;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPClient {

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(6000)) {

            byte[] buffer = new byte[256];

            System.out.println("Cliente UDP escuchando...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String mensaje = new String(packet.getData(), 0, packet.getLength());
                System.out.println("UDP recibido: " + mensaje);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
