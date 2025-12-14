//AreAri
//todo lo que es conexion de la bd esta aqui, cualquier error de bd es aqui 
//asi que si andas estresada revisa aqui primero 

package proyectofinal.db;
import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseManager {
    
    private Connection conexion;
    private String urlConexion;
    private String usuario;
    private String contrasena;
    
    public DatabaseManager() {
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        Properties config = new Properties();
        
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            
            if (inputStream != null) {
                config.load(inputStream);
                System.out.println("Configuración cargada desde resources/config.properties");
            } else {
                System.out.println("config.properties no encontrado en resources. Buscando en directorio actual...");

                java.io.File archivo = new java.io.File("config.properties");
                if (archivo.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(archivo)) {
                        config.load(fis);
                        System.out.println("Configuración cargada desde ./config.properties");
                    }
                } else {
                    throw new IOException("No se encontró config.properties en ningún lugar");
                }
            }
            
            urlConexion = config.getProperty("db.url");
            usuario = config.getProperty("db.usuario");
            contrasena = config.getProperty("db.contrasena");
            
            if (urlConexion == null || usuario == null || contrasena == null) {
                throw new IOException("Faltan propiedades en config.properties");
            }
            
            System.out.println("Configuración cargada correctamente");
            System.out.println("   URL: " + urlConexion);
            System.out.println("   Usuario: " + usuario);
            
        } catch (IOException e) {
            System.err.println("\nERROR: No se pudo cargar config.properties" + e.getMessage());
            urlConexion = "jdbc:postgresql://localhost:5432/dispositivos_db";
            usuario = "postgres";
            contrasena = "1316"; 
        }
    }
    public boolean conectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                return true; 
            }
            
            System.out.println("Conectando a la bd...");
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(urlConexion, usuario, contrasena);
            System.out.println("Conexión exitosa a: " + conexion.getCatalog());
            return true;
            
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL no encontrado");
            return false;
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    public void desconectar() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión finalizada");
            } catch (SQLException e) {
            }
        }
    }
    public ResultSet ejecutarConsulta(String sql) {
        try {
            if (!conectar()) return null;
            
            Statement stmt = conexion.createStatement();
            return stmt.executeQuery(sql);
            
        } catch (SQLException e) {
            System.err.println("Error en consulta: " + e.getMessage());
            return null;
        }
    }

    public int ejecutarActualizacion(String sql) {
        try {
            if (!conectar()) return -1;
            
            Statement stmt = conexion.createStatement();
            return stmt.executeUpdate(sql);
            
        } catch (SQLException e) {
            System.err.println("Error en actualización: " + e.getMessage());
            return -1;
        }
    }

    public Connection getConexion() {
        return conexion;
    }
    
    public void mostrarInfoConexion() {
        if (conexion == null) {
            System.out.println("No hay conexión activa");
            return;
        }
        
        try {
            System.out.println("\nINFORMACIÓN DE CONEXIÓN:");
            System.out.println("Base de datos: " + conexion.getCatalog());
            System.out.println("PostgreSQL v" + conexion.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {

        }
    }
}