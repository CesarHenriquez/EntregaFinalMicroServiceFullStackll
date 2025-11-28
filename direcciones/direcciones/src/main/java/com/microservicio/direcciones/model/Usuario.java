package com.microservicio.direcciones.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo auxiliar de usuarios")
public class Usuario {
    @Schema(description = "ID del usuario", example = "1")
    private Long id;
    @Schema(description = "Nickname del usuario", example = "usuario123")
    private String nickname;
    @Schema(description = "Clave del usuario", example = "claveSegura123")
    private String clave;
    @Schema(description = "Correo electrónico del usuario", example = "cliente1@correo.com")
    private String correo;
    @Schema(description = "Rol del usuario", example = "CLIENTE")
    private Rol rol;

}

