package com.microservicio.ventas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "detalle_venta")
@Schema(description = "Modelo de Detalle de Venta")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_id")
    @Schema(description = "ID del detalle de venta", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Venta asociada a este detalle")
    private Venta venta;

    @Column(name = "producto_id", nullable = false)
    @Schema(description = "ID del producto vendido", example = "101")
    private Long productoId;

    @Column(name = "cantidad", nullable = false)
    @Schema(description = "Cantidad del producto vendido", example = "2")
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    @Schema(description = "Precio unitario del producto", example = "49.99")
    private Double precioUnitario;

}
