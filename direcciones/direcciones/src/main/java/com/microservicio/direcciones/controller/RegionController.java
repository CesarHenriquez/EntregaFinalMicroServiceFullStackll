package com.microservicio.direcciones.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.direcciones.model.Region;
import com.microservicio.direcciones.service.RegionService;

// Imports de Swagger (OpenAPI)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/regiones")
@Tag(name = "Regiones", description = "Controlador para gestionar las Regiones geográficas")
public class RegionController {
     private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @Operation(summary = "Listar todas las regiones", description = "Devuelve una lista completa de las regiones disponibles en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Region.class)))
    })
    @GetMapping
    public List<Region> listar() {
        return regionService.listar();
    }

    @Operation(summary = "Guardar nueva región", description = "Permite registrar una nueva región en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Región guardada correctamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Region.class)))
    })
    @PostMapping
    public Region guardar(@RequestBody Region region) {
        return regionService.guardar(region);
    }

}