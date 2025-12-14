//AreAri
//aqui se ejecuta todo y asi ya no abres tantos cmds, asi que recuedalo cabeza de chorlito
//dormida esta por eso de una vez me adelanto a la ari del futuro
package proyectofinal;

import proyectofinal.server.Servidor;
import proyectofinal.client.ClienteGUI;
import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args) {
        System.out.println("  SISTEMA DE GESTIÓN DE DISPOSITIVOS");
        System.out.println("  Proyecto Final - Programación en Red - AreAri");
        
        // decidimos si iniciar el servidor o el cliente 
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            System.out.println("Iniciando servidor...");
            iniciarServidor();
        } else {
            System.out.println("Iniciando cliente...");
            iniciarCliente();
        }
    }
    
    //iniciamos el servidor TCP
    private static void iniciarServidor() {
        try {
            Servidor servidor = new Servidor();
            servidor.iniciar();
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //interfaz del cliente
    private static void iniciarCliente() {
        // SwingUtilities.invokeLater asegura que la GUI se ejecute en el hilo correcto
        SwingUtilities.invokeLater(() -> {
            try {
                ClienteGUI cliente = new ClienteGUI();
                cliente.mostrarVentana();
            } catch (Exception e) {
                System.err.println("Error al iniciar el cliente: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}