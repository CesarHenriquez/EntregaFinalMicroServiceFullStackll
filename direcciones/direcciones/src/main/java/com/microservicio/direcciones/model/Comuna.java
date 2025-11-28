package com.microservicio.direcciones.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo de comunas")
public class Comuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comuna_id")
    @Schema(description = "ID de la comuna", example = "1")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    @Schema(description = "Nombre de la comuna", example = "Santiago")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    @Schema(description = "Región a la que pertenece la comuna")
    private Region region;

}