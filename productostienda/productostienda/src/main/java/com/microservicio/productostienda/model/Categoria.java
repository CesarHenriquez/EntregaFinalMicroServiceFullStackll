package com.microservicio.productostienda.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categoria")
@Schema(description = "Modelo de Categoría para la gestión de categorías en el sistema")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    @Schema(description = "ID de la categoría", example = "1")
    private Long id;
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    @Schema(description = "Nombre de la categoría", example = "Discos")
    private String nombre;

}
