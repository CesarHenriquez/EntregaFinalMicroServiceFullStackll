package com.microservicio.direcciones.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Schema(description = "ID del rol", example = "1")
    private Long id;
    @Schema(description = "Nombre del rol", example = "CLIENTE")
    private String nombre;

}