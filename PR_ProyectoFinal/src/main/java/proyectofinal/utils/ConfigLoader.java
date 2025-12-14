//AreAri
//ok aqui esta lo de la carga de configuracion desde un archivo externo o interno
//para asi évitar estar cambiando el codigo cada vez que cambie un puerto o algo asi
//y evitar que mi usuario entre al codigo 
package proyectofinal.utils;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties propiedades;
    
    public ConfigLoader() {
        propiedades = new Properties();
        cargarConfiguracion();
    }
    
    private void cargarConfiguracion() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            
            if (inputStream != null) {
                propiedades.load(inputStream);
                System.out.println("Configuración cargada desde resources/config.properties");
            } else {
                System.err.println("No se encontró config.properties en resources");
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando configuración: " + e.getMessage());
        }
    }
    
    public String getString(String clave) {
        return propiedades.getProperty(clave, "");
    }
    
    public int getInt(String clave) {
        try {
            return Integer.parseInt(propiedades.getProperty(clave, "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public boolean getBoolean(String clave) {
        return Boolean.parseBoolean(propiedades.getProperty(clave, "false"));
    }
}