package com.microservicio.registrousuario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Registro: Público (para que cualquiera cree cuenta)
                .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                
                // 2. Listar Usuarios: Permitimos acceso público temporalmente para que
                //    el Panel de Vendedor pueda leer la lista enviando el token desde React.
                //    (Si tuvieras el filtro JWT completo aquí, pondríamos .authenticated())
                .requestMatchers(HttpMethod.GET, "/usuarios").permitAll() // <--- ESTA LÍNEA ARREGLA TU PANEL
                
                // 3. Resto: Permitir para evitar bloqueos inesperados en la demo
                .anyRequest().permitAll()
            );
        return http.build();
    }
}