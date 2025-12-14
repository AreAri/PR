//AreAri
//aqui es como el main pero del servidor
//solo para probar
    //porque al probar todo junto salio mal y no supe que hacer
package proyectofinal.server;
import proyectofinal.utils.ConfigLoader;

public class ServidorPrincipal {
    public static void main(String[] args) {
        System.out.println("INICIANDO SERVIDOR DE DISPOSITIVOS");
        ConfigLoader config = new ConfigLoader();
      
        int puertoTCP = config.getInt("server.tcp.port");
        int maxConexiones = config.getInt("server.max.connections");
        if (puertoTCP == 0) puertoTCP = 12345;
        if (maxConexiones == 0) maxConexiones = 10;
        
        System.out.println("Configuracion cargada:");
        System.out.println("  Puerto TCP: " + puertoTCP);
        System.out.println("  Max conexiones: " + maxConexiones);

        ServidorTCP servidor = new ServidorTCP(puertoTCP, maxConexiones);
        
        //el shutdown es para evitar problemas al cerrar el servidor
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Recibida señal de apagado...");
            servidor.detener();
        }));
        
        servidor.iniciar();
    }
}