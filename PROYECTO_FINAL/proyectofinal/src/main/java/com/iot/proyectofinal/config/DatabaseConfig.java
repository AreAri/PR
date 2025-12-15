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
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Bean
    public DataSource dataSource() {
        System.out.println("🔌 Configurando conexión a PostgreSQL...");
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
            boolean isValid = connection.isValid(2); // 2 segundos de timeout
            
            if (isValid) {
                System.out.println("CONEXIÓN A POSTGRESQL EXITOSA!");
                System.out.println("Base de datos: iot_management_db");
                System.out.println("URL: " + dbUrl);
            } else {
                System.err.println("CONEXIÓN FALLIDA");
            }
            
            connection.close();
            return isValid;
            
        } catch (SQLException e) {
            System.err.println("ERROR DE CONEXIÓN: " + e.getMessage());
            System.err.println("Verifica:");
            System.err.println("  1. PostgreSQL está corriendo");
            System.err.println("  2. La BD 'iot_management_db' existe");
            System.err.println("  3. Usuario/contraseña correctos");
            return false;
        }
    }
}