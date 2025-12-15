package com.iot.proyectofinal.controller;

import com.iot.proyectofinal.socket.TCPServer;
import com.iot.proyectofinal.socket.UDPServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sockets")
public class SocketController {
    
    @Autowired(required = false)
    private TCPServer tcpServer;
    
    @Autowired(required = false)
    private UDPServer udpServer;
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSocketStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Estado TCP
        Map<String, Object> tcpStatus = new HashMap<>();
        if (tcpServer != null) {
            tcpStatus.put("port", 8081);  // Valor fijo
            tcpStatus.put("running", tcpServer.estaCorriendo());
            tcpStatus.put("stats", tcpServer.getStats());
        } else {
            tcpStatus.put("port", 8081);
            tcpStatus.put("running", false);
            tcpStatus.put("error", "TCP Server no disponible");
        }
        status.put("tcp", tcpStatus);
        
        // Estado UDP
        Map<String, Object> udpStatus = new HashMap<>();
        if (udpServer != null) {
            udpStatus.put("port", 8082);  // Valor fijo
            udpStatus.put("running", udpServer.estaCorriendo());
        } else {
            udpStatus.put("port", 8082);
            udpStatus.put("running", false);
            udpStatus.put("error", "UDP Server no disponible");
        }
        status.put("udp", udpStatus);
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/tcp/broadcast")
    public ResponseEntity<Map<String, String>> broadcastTCP(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message != null && !message.trim().isEmpty() && tcpServer != null) {
            tcpServer.broadcast(message);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Mensaje broadcast enviado");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/tcp/clients")
    public ResponseEntity<Map<String, Object>> getConnectedClients() {
        if (tcpServer != null) {
            Map<String, Object> response = tcpServer.getStats();
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(new HashMap<>());
    }
}