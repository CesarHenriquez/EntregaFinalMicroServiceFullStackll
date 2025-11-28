package com.microservicio.direcciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 1. Permite todas las rutas de la API
                        .allowedOrigins("http://localhost:3000", "http://localhost:5173","http://localhost:5175") // 2. Puertos de React (CreateReactApp y Vite)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. Todos los verbos HTTP
                        .allowedHeaders("*") // 4. Permite enviar el header Authorization (Token)
                        .allowCredentials(true);
            }
        };
    }

}
