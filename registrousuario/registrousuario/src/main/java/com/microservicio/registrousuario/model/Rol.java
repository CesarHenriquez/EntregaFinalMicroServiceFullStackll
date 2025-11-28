package com.microservicio.registrousuario.model;

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
@Table(name = "rol")
@Schema(description = "Modelo de Rol para registro de usuarios")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    @Schema(description = "ID único del rol", example = "1")
    private Long id;
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombre;

}
