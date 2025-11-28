package com.microservicio.productostienda.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Modelo de Rol para la gestión de roles en el sistema")
public class Rol {
    @Schema(description = "ID del rol", example = "4")
    private Long id;
    @Schema(description = "Nombre del rol", example = "ADMINISTRADOR")
    private String nombre;

    public Rol(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

}
