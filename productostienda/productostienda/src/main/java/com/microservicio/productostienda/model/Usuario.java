package com.microservicio.productostienda.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Usuario {
    @Schema(description = "ID del usuario", example = "1")
    private Long id;
    @Schema(description = "Nickname del usuario", example = "usuario123")
    private String nickname;
    @Schema(description = "Correo electrónico del usuario", example = "usuario123@gmail.com")
    private String correo;
    @Schema(description = "Clave del usuario", example = "claveSegura123")
    private String clave;
    @Schema(description = "Rol del usuario", example = "ADMINISTRADOR")
    private Rol rol;

    public Usuario(Long id, String nickname, String correo, String clave, Rol rol) {
        this.id = id;
        this.nickname = nickname;
        this.correo = correo;
        this.clave = clave;
        this.rol = rol;
    }

}
