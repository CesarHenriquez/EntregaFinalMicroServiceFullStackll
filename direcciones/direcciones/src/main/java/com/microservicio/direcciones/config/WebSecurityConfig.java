package com.microservicio.direcciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Guardar Dirección: Lo dejamos abierto para que el Checkout no falle
                .requestMatchers(HttpMethod.POST, "/api/direcciones").permitAll()

                // 2. Listar Direcciones: Lo abrimos para que el Panel de Vendedor las vea
                .requestMatchers(HttpMethod.GET, "/api/direcciones").permitAll() // <--- ESTA LÍNEA ARREGLA TU PANEL
                
                .anyRequest().permitAll()
            );
        return http.build();
    }
}