package com.microservicio.registrousuario.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.registrousuario.model.Usuario;
import com.microservicio.registrousuario.service.UsuarioService;
import com.microservicio.registrousuario.util.JwtUtil;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión completa de usuarios: registro, listados y administración de perfiles")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    private ResponseEntity<String> validarRol(String authorizationHeader, String... requiredRoles) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token JWT no proporcionado o formato inválido.");
        }

        String rol = jwtUtil.extractRole(authorizationHeader);

        if (rol == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido o expirado.");
        }

        boolean roleAllowed = Arrays.stream(requiredRoles)
                .anyMatch(r -> r.equalsIgnoreCase(rol));

        if (!roleAllowed) {
            String rolesStr = Arrays.toString(requiredRoles).replaceAll("[\\[\\]]", "");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: solo roles " + rolesStr + " pueden realizar esta acción.");
        }

        return null; 
    }

  

    @Operation(summary = "Buscar por Nickname (Interno)", description = "Busca un usuario por su nickname. Uso interno entre microservicios.")
    @GetMapping("/interno/nickname/{nickname}")
    @ResponseStatus(HttpStatus.OK)
    public Usuario buscarPorNicknameInterno(@PathVariable String nickname) {
        return usuarioService.buscarPorNickname(nickname);
    }
  
    @Operation(summary = "Actualizar solo la clave", description = "Permite cambiar la contraseña de un usuario identificándolo por su correo.")
    // Documentación del Body para Swagger
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(mediaType = "application/json", 
                           schema = @Schema(example = "{\n  \"nuevaClave\": \"NuevaPass123\"\n}"))
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clave actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Falta el campo 'nuevaClave'"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/clave/{email}")
    public ResponseEntity<?> actualizarClave(@PathVariable String email, @RequestBody Map<String, String> payload) {
        String nuevaClave = payload.get("nuevaClave");

        if (nuevaClave == null || nuevaClave.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta el campo 'nuevaClave'.");
        }

        try {
            Usuario actualizado = usuarioService.actualizarClavePorEmail(email, nuevaClave);
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado para actualizar clave.");
            }
            return ResponseEntity.ok("Clave actualizada con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la clave: " + e.getMessage());
        }
    }

    @Operation(summary = "Buscar por Email (Interno)", description = "Busca un usuario por su correo electrónico. Uso interno para login.")
    @GetMapping("/interno/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmailInterno(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarPorCorreo(email);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(usuario);
    }
    
    //  Endpoints Públicos 

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta de usuario nueva. Asigna rol CLIENTE por defecto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Error al crear usuario (datos inválidos o duplicados)")
    })
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario creado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear usuario: " + e.getMessage());
        }
    }
    
    // Endpoints Protegidos

    @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios registrados. Requiere Rol ADMIN o DELIVERY.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (Rol insuficiente)")
    })
    @GetMapping
    public ResponseEntity<?> listar(
        @Parameter(hidden = true) 
        @RequestHeader("Authorization") String authorizationHeader
    ) {
       
        ResponseEntity<String> validationResult = validarRol(authorizationHeader, "DELIVERY", "ADMINISTRADOR");
        if (validationResult != null) {
            return validationResult;
        }

        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico por su ID. Requiere Rol ADMIN o DELIVERY.")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
      
        ResponseEntity<String> validationResult = validarRol(authorizationHeader, "ADMINISTRADOR", "DELIVERY");
        if (validationResult != null) {
            return validationResult;
        }

        Optional<Usuario> optional = usuarioService.obtenerPorId(id);
        Usuario usuario = optional.orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente. Requiere Rol ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuario,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        
        ResponseEntity<String> validationResult = validarRol(authorizationHeader, "ADMINISTRADOR");
        if (validationResult != null) {
            return validationResult;
        }

        Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
        if (actualizado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado para actualizar.");
        }
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar usuario", description = "Borra un usuario del sistema. Requiere Rol ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        
        ResponseEntity<String> validationResult = validarRol(authorizationHeader, "ADMINISTRADOR");
        if (validationResult != null) {
            return validationResult;
        }

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}