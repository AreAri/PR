package com.iot.proyectofinal;

import com.iot.proyectofinal.socket.TCPClient;
import java.util.Scanner;

public class TestSockets {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE SOCKETS TCP/UDP ===");
        System.out.println("1. Probar cliente TCP");
        System.out.println("2. Probar comunicacion manual");
        System.out.print("Seleccione opcion: ");
        
        Scanner scanner = new Scanner(System.in);
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir newline
        
        if (opcion == 1) {
            pruebaClienteTCP();
        } else if (opcion == 2) {
            pruebaManualTCP();
        } else {
            System.out.println("Opcion no valida");
        }
        
        scanner.close();
    }
    
    private static void pruebaClienteTCP() {
        System.out.println("\n=== Prueba automatica cliente TCP ===");
        TCPClient cliente = new TCPClient("localhost", 8081);
        cliente.pruebaComunicacion();
    }
    
    private static void pruebaManualTCP() {
        System.out.println("\n=== Prueba manual cliente TCP ===");
        TCPClient cliente = new TCPClient("localhost", 8081);
        
        if (cliente.conectar()) {
            Scanner scanner = new Scanner(System.in);
            String comando;
            
            System.out.println("Escriba comandos (DISCONNECT para salir):");
            
            do {
                System.out.print("> ");
                comando = scanner.nextLine();
                
                if (!comando.equalsIgnoreCase("DISCONNECT")) {
                    String respuesta = cliente.enviarMensaje(comando);
                    System.out.println("Servidor: " + respuesta);
                }
            } while (!comando.equalsIgnoreCase("DISCONNECT"));
            
            cliente.desconectar();
            scanner.close();
        }
    }
}