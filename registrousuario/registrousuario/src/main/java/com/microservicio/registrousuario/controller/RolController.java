package com.microservicio.registrousuario.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.microservicio.registrousuario.model.Rol;
import com.microservicio.registrousuario.service.RolService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Gestión de Roles de usuario (Admin, Cliente, Delivery)")
public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @Operation(summary = "Listar roles", description = "Obtiene una lista completa de todos los roles disponibles en el sistema. Incluye enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public CollectionModel<EntityModel<Rol>> listar() {
        List<Rol> roles = rolService.listarRoles();

        List<EntityModel<Rol>> rolesModel = roles.stream()
            .map(rol -> EntityModel.of(rol,
                    linkTo(methodOn(RolController.class).obtener(rol.getId())).withSelfRel()))
            .collect(Collectors.toList());

        return CollectionModel.of(rolesModel,
                linkTo(methodOn(RolController.class).listar()).withSelfRel());
    }

    @Operation(summary = "Obtener rol por ID", description = "Busca y devuelve un rol específico por su ID. Incluye enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol encontrado", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class))),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public EntityModel<Rol> obtener(
            @Parameter(description = "ID del rol a buscar", example = "1") 
            @PathVariable Long id) {
        Rol rol = rolService.obtenerPorId(id).orElse(null);
        
        return EntityModel.of(rol,
                linkTo(methodOn(RolController.class).obtener(id)).withSelfRel(),
                linkTo(methodOn(RolController.class).listar()).withRel("todos-los-roles"));
    }

    @Operation(summary = "Crear rol", description = "Registra un nuevo rol en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol creado exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida (datos faltantes)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public EntityModel<Rol> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo rol", required = true,
                    content = @Content(schema = @Schema(example = "{\n  \"nombre\": \"SUPERVISOR\"\n}")))
            @RequestBody Rol rol) {
        Rol creado = rolService.guardarRol(rol);
        return EntityModel.of(creado,
                linkTo(methodOn(RolController.class).obtener(creado.getId())).withSelfRel(),
                linkTo(methodOn(RolController.class).listar()).withRel("todos-los-roles"));
    }
}