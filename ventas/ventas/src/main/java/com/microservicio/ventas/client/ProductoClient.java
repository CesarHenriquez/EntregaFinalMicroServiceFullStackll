package com.microservicio.ventas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservicio.ventas.model.Producto;

import reactor.core.publisher.Mono;

@Component
public class ProductoClient {
    private final WebClient webClient;

    public ProductoClient(WebClient.Builder builder, @Value("${producto-service.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<Producto> obtenerProductoPorId(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Producto.class);
    }

}
