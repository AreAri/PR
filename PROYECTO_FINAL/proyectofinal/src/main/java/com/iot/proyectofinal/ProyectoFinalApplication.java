package com.iot.proyectofinal;

import com.iot.proyectofinal.socket.TCPServer;
import com.iot.proyectofinal.socket.UDPServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ProyectoFinalApplication {
    
    @Autowired
    private TCPServer tcpServer;
    
    @Autowired
    private UDPServer udpServer;
    
    public static void main(String[] args) {
        SpringApplication.run(ProyectoFinalApplication.class, args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("\n" +
            "PROYECTO FINAL PR - SISTEMA IoT CLIENTE-SERVIDOR\n" +
            "REST API:      http://localhost:8080/api\n" +
            "TCP Socket:    puerto " + (tcpServer != null ? tcpServer.getPuerto() : 8081) + "\n" +
            "UDP Socket:    puerto " + (udpServer != null ? udpServer.getPuerto() : 8082) + "\n" +
            "Base de datos: PostgreSQL iot_management_db\n" +
            "Endpoints disponibles:\n" +
            "GET  /api/dispositivos           - Listar dispositivos\n" +
            "GET  /api/dispositivos/{id}      - Obtener dispositivo\n" +
            "POST /api/dispositivos           - Crear dispositivo\n" +
            "PUT  /api/dispositivos/{id}      - Actualizar dispositivo\n" +
            "DELETE /api/dispositivos/{id}    - Eliminar dispositivo\n" +
            "POST /api/usuarios/login         - Autenticar usuario\n");
    }
}