package com.microservicio.ventas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.ventas.dto.ProductoDetalleDTO;
import com.microservicio.ventas.model.DetalleVenta;
import com.microservicio.ventas.model.Venta;
import com.microservicio.ventas.service.VentaService;
import com.microservicio.ventas.util.JwtUtil;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Gestión de órdenes de compra, historial y entregas")
public class VentaController {
    private final VentaService ventaService;
    private final JwtUtil jwtUtil;

    public VentaController(VentaService ventaService, JwtUtil jwtUtil) {
        this.ventaService = ventaService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Registrar nueva venta (Checkout)", description = "Crea una orden de compra. Lee el ID de usuario desde el cuerpo de la solicitud. No requiere Token.")
  
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos de la venta",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(example = "{\n  \"usuarioId\": 1,\n  \"direccionId\": 1,\n  \"detalles\": [\n    { \"productoId\": 1, \"cantidad\": 2 },\n    { \"productoId\": 3, \"cantidad\": 1 }\n  ]\n}")
        )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta registrada exitosamente", 
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
            @ApiResponse(responseCode = "400", description = "Faltan datos (usuarioId, direccionId)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody Map<String, Object> payload) {
        try {
            if (!payload.containsKey("usuarioId")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta el campo 'usuarioId' en el JSON.");
            }
            
            Long usuarioId = Long.valueOf(payload.get("usuarioId").toString());
            String rol = "CLIENTE";

            return ventaService.registrarVenta(usuarioId, rol, payload);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la venta: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar todas las ventas", description = "Obtiene el historial completo de ventas registradas en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class)))
    })
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

    @Operation(summary = "Listar detalles de venta", description = "Obtiene la lista completa de detalles (productos vendidos) de todas las órdenes.")
    @GetMapping("/detalles")
    public List<DetalleVenta> listarDetalles() {
        return ventaService.listarDetalles();
    }

    @Operation(summary = "Historial por Usuario", description = "Obtiene las compras realizadas por un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente", 
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron ventas para este usuario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") 
            @PathVariable Long id) {
        try {
            List<Venta> ventas = ventaService.listarPorUsuarioId(id);
            if (ventas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron ventas para el usuario con ID: " + id);
            }
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar por Dirección", description = "Obtiene las ventas asociadas a una dirección de envío específica.")
    @GetMapping("/direccion/{id}")
    public List<Venta> listarPorDireccion(@PathVariable Long id) {
        return ventaService.listarPorDireccionId(id);
    }

    @Operation(summary = "Obtener productos de una venta", description = "Devuelve la lista de productos y cantidades de una orden específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos encontrados", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDetalleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Venta sin productos o no encontrada")
    })
    @GetMapping("/{id}/productos")
    public ResponseEntity<?> obtenerProductosDeVenta(
            @Parameter(description = "ID de la venta", example = "1") 
            @PathVariable Long id) {
        try {
            List<ProductoDetalleDTO> productos = ventaService.obtenerProductosPorVentaId(id);
            if (productos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron productos.");
            }
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Subir Comprobante (Delivery)", description = "Permite al repartidor marcar una venta como entregada y subir la evidencia (foto).")
  
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(example = "{ \"proofUri\": \"content://media/external/images/media/1024\" }")
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrega confirmada exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "400", description = "Falta 'proofUri'"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @PutMapping("/{id}/proof")
    public ResponseEntity<?> setDeliveryProof(
            @Parameter(description = "ID de la venta a confirmar", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String proofUri = payload.get("proofUri");
        if (proofUri == null || proofUri.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta 'proofUri'.");
        }

        try {
            Venta ventaActualizada = ventaService.setProofAndMarkDelivered(id, proofUri);
            if (ventaActualizada != null) {
                return ResponseEntity.ok(ventaActualizada);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venta no encontrada.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}