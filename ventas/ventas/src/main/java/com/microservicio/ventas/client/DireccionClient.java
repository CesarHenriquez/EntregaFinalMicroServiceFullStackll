package com.microservicio.ventas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservicio.ventas.model.Direccion;

import reactor.core.publisher.Mono;

@Component
public class DireccionClient {
    private final WebClient webClient;

    public DireccionClient(WebClient.Builder builder, @Value("${direccion-service.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<Direccion> obtenerDireccionPorId(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Direccion.class);
    }

}
