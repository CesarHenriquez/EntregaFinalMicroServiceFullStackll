package com.microservicio.productostienda.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.productostienda.model.Categoria;
import com.microservicio.productostienda.service.CategoriaService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("api/categorias")
@Tag(name = "Categorías", description = "Gestión de las categorías de productos (Ej: Pesas, Accesorios)")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Listar todas las categorías", description = "Obtiene la lista completa de categorías disponibles para clasificar productos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listarCategorias();
    }

    @Operation(summary = "Crear una nueva categoría", description = "Registra una nueva categoría en el sistema.")
   
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos de la nueva categoría",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(example = "{\n  \"nombre\": \"Suplementos\"\n}")
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría creada correctamente", // Spring retorna 200 por defecto aquí
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        return categoriaService.guardarCategoria(categoria);
    }
}
