//areari
//el main del cliente TCP sirve para iniciar la conexion con el servidor TCP 
//y comenzar la comunicacion entre ambos
package com.example;
import com.example.tcp.TCPClient;

public class MainClient {

    public static void main(String[] args) {
        TCPClient cliente = new TCPClient("localhost", 5000);
        cliente.start();
    }
}
