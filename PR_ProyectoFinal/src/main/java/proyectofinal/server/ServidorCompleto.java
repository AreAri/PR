//areari
//unimos todo aqui
package proyectofinal.server;
import proyectofinal.utils.ConfigLoader;

public class ServidorCompleto {
    
    private ServidorTCP servidorTCP;
    private ServidorUDP servidorUDP;
    private ConfigLoader config;
    
    public ServidorCompleto() {
        this.config = new ConfigLoader();
    }
    
    
    public void iniciar() {
        System.out.println("INICIANDO SERVIDOR UDP/TCP");
        
        try {
            int puertoTCP = config.getInt("server.tcp.port");
            int puertoUDP = config.getInt("server.udp.port");
            int maxConexiones = config.getInt("server.max.connections");
            
            if (puertoTCP == 0) puertoTCP = 12345;
            if (puertoUDP == 0) puertoUDP = 12346;
            if (maxConexiones == 0) maxConexiones = 10;
            
            System.out.println("Configuracion:");
            System.out.println("  Puerto TCP: " + puertoTCP);
            System.out.println("  Puerto UDP: " + puertoUDP);
            System.out.println("  Max conexiones TCP: " + maxConexiones);
           
            System.out.println("Iniciando servidor UDP...");
            servidorUDP = new ServidorUDP(puertoUDP);
            Thread hiloUDP = new Thread(() -> {
                servidorUDP.iniciar();
            });
            hiloUDP.start();
            
            Thread.sleep(1000);
            
            // EMPEZAMOS CON TCP
            System.out.println("Iniciando servidor TCP...");
            servidorTCP = new ServidorTCP(puertoTCP, maxConexiones);
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Deteniendo servidores...");
                detener();
            }));
           
            servidorTCP.iniciar();
            
        } catch (Exception e) {
            System.err.println("ERROR iniciando servidores: " + e.getMessage());
            detener();
        }
    }
    
    //DETENEMOS TODO 
    public void detener() {
        System.out.println("Deteniendo servidores...");
        
        if (servidorTCP != null) {
            servidorTCP.detener();
        }
        
        if (servidorUDP != null) {
            servidorUDP.detener();
        }
        
        System.out.println("Servidores detenidos correctamente");
    }

    public static void main(String[] args) {
        ServidorCompleto servidor = new ServidorCompleto();
        servidor.iniciar();
    }
}