package com.microservicio.autenticarusuario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo de roles de usuario")
public class Rol {
    @Schema(description = "ID del rol", example = "1")
    private Long id;
    @Schema(description = "Nombre del rol", example = "CLIENTE")
    private String nombre;


}

