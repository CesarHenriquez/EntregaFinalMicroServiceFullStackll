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
@Schema(description = "Modelo de direcciones de usuario")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la dirección", example = "1")
    private Long id;
    @Column(name = "calle", nullable = false, length = 150)
    @Schema(description = "Calle de la dirección", example = "Avenida Siempre Viva")
    private String calle;
    @Column(name = "numero", nullable = false)
    @Schema(description = "Número de la dirección", example = "742")
    private String codigoPostal;
    @Column(name = "usuario_id", nullable = false)
    @Schema(description = "ID del usuario asociado a la dirección", example = "1")
    private Long usuarioId; 

    @ManyToOne
    @JoinColumn(name = "comuna_id")
    @Schema(description = "Comuna asociada a la dirección")
    private Comuna comuna;

}
