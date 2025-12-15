package com.iot.proyectofinal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {
    
    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/iot_management_db}")
    private String dbUrl;
    
    @Value("${spring.datasource.username:postgres}")
    private String dbUsername;
    
    @Value("${spring.datasource.password:1316}")
    private String dbPassword;
    
    @Bean
    public DataSource dataSource() {
        System.out.println("Configurando conexión a PostgreSQL...");
        System.out.println("URL: " + dbUrl);
        System.out.println("Usuario: " + dbUsername);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        
        return dataSource;
    }
    
    @Bean
    public boolean testConnection() {
        try {
            Connection connection = dataSource().getConnection();
            boolean isValid = connection.isValid(2);
            
            if (isValid) {
                System.out.println("CONEXIÓN A POSTGRESQL EXITOSA!");
                System.out.println("   Base de datos: " + dbUrl);
            } else {
                System.err.println("CONEXIÓN FALLIDA");
            }
            
            connection.close();
            return isValid;
            
        } catch (SQLException e) {
            System.err.println("ERROR DE CONEXIÓN POSTGRESQL: " + e.getMessage());
            System.err.println("   Verifica:");
            System.err.println("   1. PostgreSQL está corriendo (services.msc)");
            System.err.println("   2. La BD 'iot_management_db' existe");
            System.err.println("   3. Usuario: postgres / Contraseña: admin");
            System.err.println("   Usando H2 temporalmente...");
            
            // Configurar H2 temporalmente
            DriverManagerDataSource h2DataSource = new DriverManagerDataSource();
            h2DataSource.setDriverClassName("org.h2.Driver");
            h2DataSource.setUrl("jdbc:h2:mem:iotdb");
            h2DataSource.setUsername("sa");
            h2DataSource.setPassword("");
            
            return false;
        }
    }
}
