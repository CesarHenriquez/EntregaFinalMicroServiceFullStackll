package com.microservicio.ventas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo de Dirección auxiliar")
public class Direccion {
     @Schema(description = "ID de la dirección", example = "1")
    private Long id;
    @Schema(description = "Calle de la dirección", example = "Av. Principal")
    private String calle;
    @Schema(description = "Número de la dirección", example = "123")
    private String numero;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

}
