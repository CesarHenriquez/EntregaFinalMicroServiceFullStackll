package com.microservicio.ventas.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaRespuestaDTO {
    private Long id;
    private Long usuarioId;
    private Long direccionId;
    private LocalDate fecha;
    private List<DetalleRespuestaDTO> detalles;

}
