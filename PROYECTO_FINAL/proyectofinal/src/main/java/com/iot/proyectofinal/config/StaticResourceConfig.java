package com.iot.proyectofinal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve archivos estáticos desde /static
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        
        // También maneja la raíz
        registry.addResourceHandler("/")
                .addResourceLocations("classpath:/static/index.html");
    }
}