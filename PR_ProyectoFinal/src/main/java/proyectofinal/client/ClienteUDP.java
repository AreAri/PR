//AreAri
//udp pero en quien confias
package proyectofinal.client;
import java.io.*;
import java.net.*;
import proyectofinal.utils.ConfigLoader;
import java.util.Scanner;

public class ClienteUDP {
    
    private DatagramSocket socketUDP;
    private InetAddress direccionServidor;
    private int puertoServidor;
    
    //cponstructor
    public ClienteUDP() {

    }
    
    public boolean configurar(String host, int puerto) {
        try {
            socketUDP = new DatagramSocket();
            direccionServidor = InetAddress.getByName(host);
            puertoServidor = puerto;
            
            System.out.println("Cliente UDP configurado");
            System.out.println("Servidor: " + host + ":" + puerto);
            System.out.println("Puerto local: " + socketUDP.getLocalPort());
            
            return true;
            
        } catch (SocketException e) {
            System.err.println("ERROR: No se pudo crear socket UDP: " + e.getMessage());
            return false;
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Host desconocido: " + host);
            return false;
        }
    }

    public void enviarMensaje(String mensaje) {
        if (socketUDP == null || direccionServidor == null) {
            System.err.println("ERROR: Cliente no configurado. Llama a configurar() primero");
            return;
        }
        
        try {
            byte[] datos = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(
                datos, datos.length, direccionServidor, puertoServidor
            );
            socketUDP.send(paquete);
            
            System.out.println("Mensaje UDP enviado: " + mensaje);
            recibirRespuesta();
            
        } catch (IOException e) {
            System.err.println("ERROR enviando mensaje UDP: " + e.getMessage());
        }
    }
    
    private void recibirRespuesta() {
        try {
           
            socketUDP.setSoTimeout(5000);
            byte[] buffer = new byte[1024];
            DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length);
            
            socketUDP.receive(paqueteRespuesta);
            String respuesta = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength());
            System.out.println("Respuesta del servidor: " + respuesta);
            
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout: No se recibio respuesta del servidor");
        } catch (IOException e) {
           
        }
    }
    
    public void enviarNotificacion(String tipo, String contenido) {
        String mensaje = tipo + "|" + contenido;
        enviarMensaje(mensaje);
    }
    
    public void cerrar() {
        if (socketUDP != null && !socketUDP.isClosed()) {
            socketUDP.close();
            System.out.println("Socket UDP cerrado");
        }
    }
 
    public static void main(String[] args) {
        ConfigLoader config = new ConfigLoader();
        String host = config.getString("server.host");
        int puertoUDP = config.getInt("server.udp.port");
        
        if (host.isEmpty()) host = "localhost";
        if (puertoUDP == 0) puertoUDP = 12346; // diferente al tcp o jusntamos los cabñles
        
        System.out.println("Cliente UDP - Conectando a " + host + ":" + puertoUDP);
        
        ClienteUDP cliente = new ClienteUDP();
        
        if (cliente.configurar(host, puertoUDP)) {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("\nModo interactivo UDP");
            System.out.println("Formato de mensajes: TIPO|CONTENIDO");
            System.out.println("  NOTIFICACION|Sensor temperatura: 25°C");
            System.out.println("  ALERTA|Temperatura critica: 40°C");
            System.out.println("  ACTUALIZACION|Dispositivo reconectado");
            System.out.println("Escribe 'exit' para salir");
            String entrada;
            while (true) {
                System.out.print("UDP> ");
                entrada = scanner.nextLine();
                
                if ("exit".equalsIgnoreCase(entrada) || "quit".equalsIgnoreCase(entrada)) {
                    break;
                }
                
                if (!entrada.contains("|")) {
                    cliente.enviarNotificacion("NOTIFICACION", entrada);
                } else {
                    cliente.enviarMensaje(entrada);
                }
            }
            
            scanner.close();
            cliente.cerrar();
        }
    }
}