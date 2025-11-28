package com.microservicio.ventas.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "venta")
@Schema(description = "Modelo de Venta")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    @Schema(description = "ID de la venta", example = "1")
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    @Schema(description = "ID del usuario que realiza la venta", example = "123")
    private Long usuarioId;

    @Column(name = "direccion_id", nullable = false)
    @Schema(description = "ID de la dirección de envío", example = "456")
    private Long direccionId;

    @Column(name = "fecha", nullable = false)
    @Schema(description = "Fecha de la venta", example = "2023-10-01")
    private LocalDate fecha;
   
    @Column(name = "delivered", nullable = false)
    @Schema(description = "Estado de entrega de la venta", example = "false")
    private Boolean delivered = false;

    @Column(name = "proof_uri", length = 255)
    @Schema(description = "URI del comprobante de entrega", example = "content://...")
    private String proofUri;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Schema(description = "Lista de detalles de la venta")
    private List<DetalleVenta> detalles;

}
