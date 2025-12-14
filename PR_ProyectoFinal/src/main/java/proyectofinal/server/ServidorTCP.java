//AreAri
//empezamos con el tcp cosa que ya conoces y solo guiaste del codigo que ya tienes 
//no deberias tener problemas aquib y si usas hilos cada cliente es un hilo solo 
package proyectofinal.server;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import proyectofinal.utils.ConfigLoader;

public class ServidorTCP {
    private ServerSocket serverSocket;
    private int puerto;
    private int maxConexiones;
    private ExecutorService poolHilos; //arberca de hilo jsjs
    private volatile boolean ejecutando;
    
    //concurrenthashmap importante para los diversos hilos 
    //todos os conectados vaan pa aca 
    private ConcurrentHashMap<String, Socket> clientesConectados;
 
    //su señor constructor
    public ServidorTCP(int puerto, int maxConexiones) {
        this.puerto = puerto;
        this.maxConexiones = maxConexiones;
        this.ejecutando = false;
        this.clientesConectados = new ConcurrentHashMap<>();
        //cada hilo osea el tiencte tenda un maximo de conexiones
        this.poolHilos = Executors.newFixedThreadPool(maxConexiones);
    }

    public void iniciar() {
        ejecutando = true;
        
        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor TCP listo en puerto: " + puerto);
            System.out.println("Esperando conexiones...");
            
            while (ejecutando) {
                try {
                    Socket socketCliente = serverSocket.accept();
                    System.out.println("Nueva conexion TCP aceptada");
                    manejarNuevoCliente(socketCliente);
                } catch (IOException e) {
                    if (ejecutando) {
                        System.err.println("Error aceptando conexion: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("No se pudo iniciar servidor TCP: " + e.getMessage());
        }
    }
    
   //NUEVOS CLIENTES
    private void manejarNuevoCliente(Socket socketCliente) {
        String ipCliente = socketCliente.getInetAddress().getHostAddress();
        int puertoCliente = socketCliente.getPort();
        String idCliente = ipCliente + ":" + puertoCliente;
        
        System.out.println("Nuevo cliente conectado: " + idCliente);
        
        //creamos una lista con los clientes conectados
        clientesConectados.put(idCliente, socketCliente);
        ManejadorCliente manejador = new ManejadorCliente(socketCliente, idCliente, this);
 
        poolHilos.execute(manejador);

        System.out.println("Clientes conectados: " + clientesConectados.size() + "/" + maxConexiones);
    }

    public void detener() {
        System.out.println("Deteniendo servidor...");
        
        ejecutando = false;
        System.out.println("Cerrando sesiones...");
        for (String idCliente : clientesConectados.keySet()) {
            try {
                Socket socket = clientesConectados.get(idCliente);
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("  Desconectado cliente: " + idCliente);
                }
            } catch (IOException e) {
            }
        }
        
        clientesConectados.clear();
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("ServerSocket cerrado");
            } catch (IOException e) {
                System.err.println("Error cerrando ServerSocket: " + e.getMessage());
            }
        }
        
        if (poolHilos != null) {
            poolHilos.shutdown();
            try {
                // eperamos a que hilos terminen 
                if (!poolHilos.awaitTermination(5, TimeUnit.SECONDS)) {
                    poolHilos.shutdownNow(); // si no termina en 5 a la fuerza termina
                }
            } catch (InterruptedException e) {
                poolHilos.shutdownNow();
            }
        }
        
        System.out.println("Servidor detenido correctamente");
    }
    
  //quitamos clientes
    public void removerCliente(String idCliente) {
        clientesConectados.remove(idCliente);
        System.out.println("Cliente desconectado: " + idCliente);
        System.out.println("Clientes conectados: " + clientesConectados.size() + "/" + maxConexiones);
    }
    
  //total de clientes conectados
    public int getClientesConectados() {
        return clientesConectados.size();
    }
    
//hola?, clientes estas ahi?
    public void broadcast(String mensaje) {
        for (Socket socket : clientesConectados.values()) {
            if (socket != null && !socket.isClosed()) {
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(mensaje);
                } catch (IOException e) {
                }
            }
        }
    }
}
