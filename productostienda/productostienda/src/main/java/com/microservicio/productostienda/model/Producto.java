package com.microservicio.productostienda.model;

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
@Table(name = "producto")
@Schema(description = "Modelo de Producto para la gestión de productos en el sistema")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    @Schema(description = "Nombre del producto", example = "Discos calibrados")
    private String nombre;

    @Column(name = "descripcion", nullable = false)
    @Schema(description = "Descripción del producto", example = "Discos calibrados de alta calidad")
    private String descripcion;

    @Column(name = "precio", nullable = false)
    @Schema(description = "Precio del producto", example = "49.99")
    private Double precio;
   
    @Column(name = "stock", nullable = false)
    @Schema(description = "Stock del producto", example = "50")
    private Integer stock = 0; 


    @Column(name = "imagen_uri", length = 255)
    @Schema(description = "URI o nombre de archivo de la imagen (Web o Local)", example = "cinturon_fit.jpg")
    private String imagenUri;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @Schema(description = "Categoría del producto")
    private Categoria categoria;

}
