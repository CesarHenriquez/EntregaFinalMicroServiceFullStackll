package com.microservicio.direcciones.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.direcciones.model.Comuna;
import com.microservicio.direcciones.model.Direccion;
import com.microservicio.direcciones.service.DireccionService;
import com.microservicio.direcciones.util.JwtUtil;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/direcciones")
@Tag(name = "Direcciones", description = "Gestión de direcciones de envío de los usuarios")
public class DireccionController {
    private final DireccionService direccionService;
    private final JwtUtil jwtUtil; 

    public DireccionController(DireccionService direccionService, JwtUtil jwtUtil) {
        this.direccionService = direccionService;
        this.jwtUtil = jwtUtil; 
    }

    @Operation(summary = "Listar todas las direcciones", description = "Obtiene un listado general de todas las direcciones registradas en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Direccion.class)))
    })
    @GetMapping
    public List<Direccion> listar() {
        return direccionService.listar();
    }

    @Operation(summary = "Listar direcciones de un usuario", description = "Obtiene las direcciones asociadas a un ID de usuario específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Direcciones encontradas", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Direccion.class)))
    })
    @GetMapping("/usuario/{usuarioId}")
    public List<Direccion> porUsuario(
            @Parameter(description = "ID del usuario dueño de las direcciones", example = "1") 
            @PathVariable Long usuarioId) {
        return direccionService.buscarPorUsuario(usuarioId);
    }

    @Operation(summary = "Registrar nueva dirección", description = "Guarda una nueva dirección para el usuario autenticado. Requiere Rol CLIENTE y Token JWT.")
   
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos de la dirección",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(example = "{\n  \"calle\": \"Av. Siempre Viva\",\n  \"codigoPostal\": \"742\",\n  \"comunaId\": 1\n}")
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dirección guardada exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Direccion.class))),
        @ApiResponse(responseCode = "400", description = "Datos faltantes o inválidos"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (Rol incorrecto)")
    })
    @PostMapping
    public ResponseEntity<?> guardar(
        @RequestBody Map<String, Object> payload,
        @RequestHeader("Authorization") String authorizationHeader 
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT no proporcionado o formato inválido.");
        }

        Long usuarioId = jwtUtil.extractUserId(authorizationHeader);
        String rol = jwtUtil.extractRole(authorizationHeader);

        if (usuarioId == null || rol == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido o expirado.");
        }

        try {
            if (!rol.equalsIgnoreCase("CLIENTE")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acceso denegado: solo usuarios con rol CLIENTE pueden registrar direcciones.");
            }

            Direccion direccion = new Direccion();
            direccion.setCalle((String) payload.get("calle"));
            direccion.setCodigoPostal((String) payload.get("codigoPostal"));
            direccion.setUsuarioId(usuarioId); 

            Comuna comuna = new Comuna();
            
            if (!payload.containsKey("comunaId")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta el campo 'comunaId' en el cuerpo de la solicitud.");
            }
            comuna.setId(Long.valueOf(payload.get("comunaId").toString()));
            direccion.setComuna(comuna);

            Direccion guardada = direccionService.guardar(direccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }
}