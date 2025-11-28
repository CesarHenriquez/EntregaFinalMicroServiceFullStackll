package com.microservicio.direcciones.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservicio.direcciones.model.Usuario;

import reactor.core.publisher.Mono;

@Component
public class UsuarioClient {
    private final WebClient webClient;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioClient(WebClient.Builder builder,
            @Value("${usuario-service.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    
    public Mono<Usuario> obtenerUsuarioPorNickname(String nickname) {
        return webClient.get()
                .uri("/nickname/{nickname}", nickname)
                .retrieve()
                .bodyToMono(Usuario.class);
    }

    
    public boolean validarClave(String claveIngresada, String claveEncriptada) {
        return passwordEncoder.matches(claveIngresada, claveEncriptada);
    }

}