package com.microservicio.direcciones.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.direcciones.model.Comuna;
import com.microservicio.direcciones.service.ComunaService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/comunas")
@Tag(name = "Comunas", description = "Gestión de las comunas asociadas a regiones")
public class ComunaController {
    private final ComunaService comunaService;

    public ComunaController(ComunaService comunaService) {
        this.comunaService = comunaService;
    }

    @Operation(summary = "Listar todas las comunas", description = "Obtiene una lista completa de todas las comunas disponibles en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comuna.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public List<Comuna> listar() {
        return comunaService.listar();
    }

    @Operation(summary = "Listar comunas por región", description = "Filtra y obtiene las comunas que pertenecen a una región específica mediante su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comunas filtrada obtenida correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comuna.class))),
        @ApiResponse(responseCode = "404", description = "Región no encontrada o sin comunas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/region/{regionId}")
    public List<Comuna> listarPorRegion(
            @Parameter(description = "ID de la región para filtrar", example = "1") 
            @PathVariable Long regionId) {
        return comunaService.listarPorRegion(regionId);
    }

    @Operation(summary = "Guardar una nueva comuna", description = "Registra una nueva comuna y la asocia a una región existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comuna guardada correctamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comuna.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida (faltan datos o región no existe)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public Comuna guardar(@RequestBody Comuna comuna) {
        return comunaService.guardar(comuna);
    }
}