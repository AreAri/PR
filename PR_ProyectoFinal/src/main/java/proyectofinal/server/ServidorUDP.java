//AreAri
//udp el inseguro pero obligao
package proyectofinal.server;
import java.io.*;
import java.net.*;
import proyectofinal.utils.ConfigLoader;

public class ServidorUDP {
    
    private DatagramSocket socketUDP;
    private int puerto;
    private volatile boolean ejecutando;
    private byte[] buffer;
    
   //constructor
    public ServidorUDP(int puerto) {
        this.puerto = puerto;
        this.ejecutando = false;
        this.buffer = new byte[1024];
    }

    public void iniciar() {
        ejecutando = true;
        
        try {
            socketUDP = new DatagramSocket(puerto);
            System.out.println("Servidor UDP listo en puerto: " + puerto);
            
            new Thread(() -> {
                while (ejecutando) {
                    try {
                        DatagramPacket paquete = recibirPaquete();
                        if (paquete != null) {
                            procesarPaquete(paquete);
                        }
                    } catch (Exception e) {
                        if (ejecutando) {
                            System.err.println("Error UDP: " + e.getMessage());
                        }
                    }
                }
            }).start();
            
        } catch (SocketException e) {
            System.err.println("No se pudo iniciar servidor UDP: " + e.getMessage());
        }
    }
    
    //paquetes udp
    private DatagramPacket recibirPaquete() throws IOException {
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socketUDP.receive(paquete);
        return paquete;
    }

    private void procesarPaquete(DatagramPacket paquete) {
        InetAddress direccionRemitente = paquete.getAddress();
        int puertoRemitente = paquete.getPort();
        String mensaje = new String(paquete.getData(), 0, paquete.getLength());
        System.out.println("UDP RECIBIDO de " + direccionRemitente.getHostAddress() + ":" + puertoRemitente);
        System.out.println("  Mensaje: " + mensaje);
        procesarMensaje(mensaje, direccionRemitente, puertoRemitente);
    }
    
    private void procesarMensaje(String mensaje, InetAddress direccion, int puerto) {
        try {
            String[] partes = mensaje.split("\\|", 2);
            
            if (partes.length >= 2) {
                String tipo = partes[0];
                String contenido = partes[1];
                
                System.out.println("  Tipo: " + tipo);
                System.out.println("  Contenido: " + contenido);
                
                // vemos que tipo de notificacion es
                switch (tipo.toUpperCase()) {
                    case "NOTIFICACION":
                        System.out.println("  Procesando notificacion...");
                        break;
                    case "ALERTA":
                        System.out.println("  ! ALERTA RECIBIDA !");
                        break;
                    case "ACTUALIZACION":
                        System.out.println("  Actualizacion recibida");
                        break;
                    default:
                        System.out.println("  Tipo desconocido: " + tipo);
                }
                guardarNotificacionEnBD(tipo, contenido, direccion.getHostAddress());
                
            } else {
                System.out.println("  Mensaje simple: " + mensaje);
            }
            enviarConfirmacion(direccion, puerto, "OK: " + mensaje.substring(0, Math.min(mensaje.length(), 20)));
            
        } catch (Exception e) {
            System.err.println("Error procesando mensaje UDP: " + e.getMessage());
        }
    }
    
    
    private void guardarNotificacionEnBD(String tipo, String contenido, String ipRemitente) {
    
        System.out.println("  [BD] Guardando notificacion:");
        System.out.println("    Tipo: " + tipo);
        System.out.println("    IP: " + ipRemitente);
        System.out.println("    Contenido: " + contenido.substring(0, Math.min(contenido.length(), 50)));
    }
    
    private void enviarConfirmacion(InetAddress direccion, int puerto, String mensaje) {
        try {
            byte[] datos = mensaje.getBytes();
            DatagramPacket paqueteRespuesta = new DatagramPacket(
                datos, datos.length, direccion, puerto
            );
            
            socketUDP.send(paqueteRespuesta);
            System.out.println("  Confirmacion enviada a " + direccion.getHostAddress() + ":" + puerto);
            
        } catch (IOException e) {
            System.err.println("Error enviando confirmacion: " + e.getMessage());
        }
    }
    
    public void enviarMensaje(String direccionIP, int puertoDestino, String mensaje) {
        try {
            InetAddress direccion = InetAddress.getByName(direccionIP);
            byte[] datos = mensaje.getBytes();
            
            DatagramPacket paquete = new DatagramPacket(
                datos, datos.length, direccion, puertoDestino
            );
            
            socketUDP.send(paquete);
            System.out.println("Mensaje UDP enviado a " + direccionIP + ":" + puertoDestino);
            
        } catch (IOException e) {
            System.err.println("Error enviando mensaje UDP: " + e.getMessage());
        }
    }
    
    public void detener() {
        System.out.println("Deteniendo servidor UDP...");
        ejecutando = false;
        
        if (socketUDP != null && !socketUDP.isClosed()) {
            socketUDP.close();
            System.out.println("Socket UDP cerrado");
        }
    }
  
    public boolean estaEjecutando() {
        return ejecutando;
    }
}
