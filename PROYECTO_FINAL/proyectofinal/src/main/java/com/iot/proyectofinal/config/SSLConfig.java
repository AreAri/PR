package com.iot.proyectofinal.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SSLConfig {
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        // Configuración SIMPLE - solo HTTPS en 8443
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setPort(8443);
        return factory;
    }
}