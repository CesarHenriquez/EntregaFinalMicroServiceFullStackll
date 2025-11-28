package com.microservicio.direcciones.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "region")
@Schema(description = "Modelo de regiones")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    @Schema(description = "ID de la región", example = "1")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    @Schema(description = "Nombre de la región", example = "Región Metropolitana")
    private String nombre;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    @Schema(description = "Lista de comunas asociadas a la región")
    @JsonIgnore 
    private List<Comuna> comunas;

}

