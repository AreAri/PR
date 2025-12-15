package com.iot.proyectofinal.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PRUEBA DE CONEXIÓN A POSTGRESQL");
        System.out.println("=".repeat(50));
        
        try (Connection connection = dataSource.getConnection()) {
            // 1. Verificar conexión
            System.out.println("Conexión establecida");
            
            // 2. Obtener información de la BD
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Base de datos: " + metaData.getDatabaseProductName());
            System.out.println("Versión: " + metaData.getDatabaseProductVersion());
            System.out.println("Usuario: " + metaData.getUserName());
            
            // 3. Verificar tablas existentes
            System.out.println("\nTABLAS EN LA BASE DE DATOS:");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            int tableCount = 0;
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   - " + tableName);
                tableCount++;
            }
            
            if (tableCount == 0) {
                System.out.println("No hay tablas. ¿Ejecutaste bd.sql?");
            } else {
                System.out.println("Total tablas: " + tableCount);
            }
            
            // 4. Contar registros en tablas importantes
            System.out.println("\nREGISTROS EN TABLAS:");
            try {
                Integer usuarios = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Integer.class);
                System.out.println(" Usuarios: " + usuarios);
            } catch (Exception e) {
                System.out.println(" Tabla 'usuarios' no encontrada");
            }
            
            try {
                Integer dispositivos = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dispositivos", Integer.class);
                System.out.println("   Dispositivos: " + dispositivos);
            } catch (Exception e) {
                System.out.println(" Tabla 'dispositivos' no encontrada");
            }
            
        } catch (Exception e) {
            System.err.println("\nERROR DE CONEXIÓN:");
            System.err.println("   Mensaje: " + e.getMessage());
            System.err.println("\nSOLUCIÓN:");
            System.err.println("   1. Asegúrate que PostgreSQL esté corriendo");
            System.err.println("   2. Verifica usuario/contraseña en application.properties");
            System.err.println("   3. Ejecuta: psql -U postgres -f bd.sql");
        }
        
        System.out.println("=".repeat(50) + "\n");
    }
}