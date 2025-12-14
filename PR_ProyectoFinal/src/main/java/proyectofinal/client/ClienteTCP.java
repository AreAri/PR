//AreAri
//lo mismo del servidor pero para el cliente aunque si cambia un poco
package proyectofinal.client;
import java.io.*;
import java.net.*;
import proyectofinal.utils.ConfigLoader;

public class ClienteTCP {
   
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private BufferedReader teclado;
    
    // conectamos al servidor
    public boolean conectar(String host, int puerto) {
        try {
            System.out.println("Conectando a servidor " + host + ":" + puerto + "...");
            
            socket = new Socket(host, puerto);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            teclado = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("Conexion establecida con el servidor");
            
            new Thread(this::recibirMensajes).start();
            return true;
            
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo conectar al servidor: " + e.getMessage());
            return false;
        }
    }
    
    //recibimos los mensajes del servidor
    private void recibirMensajes() {
        try {
            String mensajeServidor;
            while ((mensajeServidor = entrada.readLine()) != null) {
                System.out.println("Servidor dice: " + mensajeServidor);
            }
        } catch (IOException e) {
            System.out.println("Desconectado del servidor");
        }
    }
    
   //el cliente tambien manda mensajes
    public void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
            System.out.println("Tu enviaste: " + mensaje);
        }
    }
    
    //y con esto podemos interactuar constantemente
    public void modoInteractivo() {
        System.out.println("\nModo interactivo activado");
        System.out.println("Escribe mensajes para enviar al servidor");
        System.out.println("Escribe 'exit' para salir");
        
        try {
            String mensajeUsuario;
            while ((mensajeUsuario = teclado.readLine()) != null) {
                if ("exit".equalsIgnoreCase(mensajeUsuario) || "quit".equalsIgnoreCase(mensajeUsuario)) {
                    enviarMensaje("exit");
                    break;
                }
                enviarMensaje(mensajeUsuario);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo entrada del teclado: " + e.getMessage());
        } finally {
            desconectar();
        }
    }
    
    //adios al servidor
    public void desconectar() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
            System.out.println("Desconectado del servidor");
        } catch (IOException e) {

        }
    }
    
    //solo para probar
    //porque al probar todo junto salio mal y no supe que hacer
    public static void main(String[] args) {
        
        ConfigLoader config = new ConfigLoader();
        
        String host = config.getString("server.host");
        int puerto = config.getInt("server.tcp.port");
        
        if (host.isEmpty()) host = "localhost";
        if (puerto == 0) puerto = 12345;
        
        System.out.println("Cliente TCP - Conectando a " + host + ":" + puerto);
        
        ClienteTCP cliente = new ClienteTCP();
        if (cliente.conectar(host, puerto)) {
            cliente.modoInteractivo();
        }
    }
}
