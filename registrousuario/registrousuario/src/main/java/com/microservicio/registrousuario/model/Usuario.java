package com.microservicio.registrousuario.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
@Schema(description = "Modelo de Usuario para registro")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    @Schema(description = "Nickname del usuario", example = "usuario123")
    private String nickname;
    @JsonProperty(access = JsonProperty.Access.READ_WRITE) // Permite leer y escribir el valor.
    @Column(name = "clave", nullable = false)
    @Schema(description = "Clave del usuario", example = "claveSegura123")
    private String clave;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    @Schema(description = "Correo electrónico del usuario", example = "usuario123@correo.com")
    private String correo;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    
    @Schema(description = "Rol asignado al usuario", example = "ADMIN")
    private Rol rol;

}
