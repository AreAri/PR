//AreAri
//ok aqui esta lo de la carga de configuracion desde un archivo externo o interno
//para asi évitar estar cambiando el codigo cada vez que cambie un puerto o algo asi
//y evitar que mi usuario entre al codigo 
package proyectofinal.utils;
import java.io.*;
import java.util.Properties;

public class ConfigLoader {
    // guaramos las propiedades
    private Properties propiedades;
 
    public ConfigLoader() {
        propiedades = new Properties();
        cargarConfiguracion();
    }
    //se carfa toda la configuracion
    private void cargarConfiguracion() {
        try {
            // se intenta cargar desde el directorio del proyecto
            File archivoConfig = new File("config.properties");
            
            if (archivoConfig.exists()) {
                // ya si no un archivo externo
                try (FileInputStream entrada = new FileInputStream(archivoConfig)) {
                    propiedades.load(entrada);
                    System.out.println("Configuración cargada desde: " + archivoConfig.getAbsolutePath());
                }
            } else {
                // y si no hay externo esta el reresources
                try (InputStream entrada = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                    if (entrada != null) {
                        propiedades.load(entrada);
                        System.out.println("Configuración cargada desde resources");
                    } else {
                        // ya de plano si no hay nada es por defecto
                        System.out.println("No se encontró config.properties. Usando valores por defecto.");
                        establecerValoresPorDefecto();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar configuración: " + e.getMessage());
            establecerValoresPorDefecto();
        }
    }
    
    //cuando de plano no tienes nada qui establecemos el por defecto
    private void establecerValoresPorDefecto() {
        System.out.println("Estamos estableciendo valores por defecto...");
        
        // Configuración de servidores
        propiedades.setProperty("servidor.tcp.puerto", "12345");
        propiedades.setProperty("servidor.udp.puerto", "12346");
        propiedades.setProperty("servidor.max.conexiones", "10");
        
        // Configuración de base de datos 
        propiedades.setProperty("db.url", "jdbc:postgresql://localhost:5432/dispositivos_db");
        propiedades.setProperty("db.usuario", "postgres");
        propiedades.setProperty("db.contrasena", "1316"); //aqui la contraseña cambia
        propiedades.setProperty("db.driver", "org.postgresql.Driver");
        
        // Configuración del cliente
        propiedades.setProperty("cliente.servidor.ip", "localhost");
        propiedades.setProperty("cliente.servidor.tcp.puerto", "12345");
        propiedades.setProperty("cliente.servidor.udp.puerto", "12346");
        
        propiedades.setProperty("modo.debug", "true");
        propiedades.setProperty("tiempo.timeout", "5000"); //seg
        
        System.out.println("✅ Valores por defecto establecidos");
    }
    
//metodos para poder obtener valores
    public String getString(String clave) {
        return propiedades.getProperty(clave, "");
    }
    
    public int getInt(String clave) {
        try {
            String valor = propiedades.getProperty(clave, "0");
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            System.out.println("⚠️  Error: No se pudo convertir a entero: " + clave);
            return 0;
        }
    }

    public boolean getBoolean(String clave) {
        String valor = propiedades.getProperty(clave, "false");
        return Boolean.parseBoolean(valor);
    }
    
  //guardamos todo
    public void guardarConfiguracion() {
        try (FileOutputStream salida = new FileOutputStream("config.properties")) {
            // Agregar un comentario al archivo
            propiedades.store(salida, "Configuración del Sistema de Dispositivos\nProyecto Final - Programación en Red");
            System.out.println("Configuración guardada en config.properties");
        } catch (IOException e) {
            System.out.println("Error al guardar configuración: " + e.getMessage());
        }
    }
   //se muestra en el cmd
    public void mostrarConfiguracion() {
        System.out.println("\n~~~CONFIGURACIÓN ACTUAL~~~");
        
        propiedades.forEach((clave, valor) -> {
            //recuerda no las contraseñlas
            if (clave.toString().contains("contrasena") || clave.toString().contains("password")) {
                System.out.printf("%-25s = %s%n", clave, "********");
            } else {
                System.out.printf("%-25s = %s%n", clave, valor);
            }
        });
    }
}