package com.iot.proyectofinal;

import com.iot.proyectofinal.socket.TCPClient;

public class TestSockets {
    public static void main(String[] args) {
        System.out.println("=== PROBANDO SOCKETS ===");
        
        // Usar el nuevo método estático
        TCPClient.pruebaTCP();
        
        System.out.println("=== PRUEBA COMPLETADA ===");
    }
}
