package com.microservicio.ventas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo de Producto auxiliar")
public class Producto {
     @Schema(description = "ID del producto", example = "1")
    private Long id;
    @Schema(description = "Nombre del producto", example = "Disco calibrado")
    private String nombre;
    @Schema(description = "Precio del producto", example = "99.99")
    private Double precio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

}
