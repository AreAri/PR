// AreAri
//solo vemos si la conexion funciona

package proyectofinal.db;

public class PruebaConexion {
    public static void main(String[] args) {
        System.out.println("Probando conexión a la bd...");
        
        // Crear DatabaseManager
        DatabaseManager db = new DatabaseManager();

        boolean conectado = db.conectar();
        
        if (conectado) {
            System.out.println("\nyei conecto");
            
            try {
                String version = db.getConexion().getMetaData().getDatabaseProductVersion();
                String bdNombre = db.getConexion().getCatalog();
                
                System.out.println("\nInformación básica:");
                System.out.println("   Base de datos: " + bdNombre);
                
            } catch (Exception e) {
            }

            db.desconectar();
            
        } else {
            System.out.println("\neste... tenemos problemas");
        }
        
    }
    
    public static boolean probar() {
        DatabaseManager db = new DatabaseManager();
        boolean ok = db.conectar();
        if (ok) {
            db.desconectar();
        }
        return ok;
    }
}