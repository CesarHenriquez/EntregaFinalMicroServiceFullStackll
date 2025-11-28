package com.microservicio.ventas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo de Usuario auxiliar")
public class Usuario {
     @Schema(description = "ID del usuario", example = "1")
    private Long id;
    @Schema(description = "Nickname del usuario", example = "usuario123")
    private String nickname;
    @Schema(description = "Correo electrónico del usuario", example = "usuario123@correo.com")
    private String correo;
    @Schema(description = "Clave de acceso del usuario", example = "claveSegura123")
    private String clave;
    @Schema(description = "Rol del usuario", example = "ADMINISTRADOR")
    private Rol rol;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

}
