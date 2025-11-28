package com.microservicio.productostienda.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.productostienda.model.Categoria;
import com.microservicio.productostienda.model.Producto;
import com.microservicio.productostienda.service.ProductoService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de productos y stock")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Listar todos los productos", description = "Devuelve el catálogo completo de productos con su stock e imagen.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public List<Producto> listar() {
        return productoService.listarProductos();
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca y devuelve los detalles de un producto específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(
            @Parameter(description = "ID único del producto", example = "1") 
            @PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Crear un nuevo producto", description = "Agrega un nuevo producto al inventario. Endpoint público (seguridad desactivada temporalmente).")
  
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del nuevo producto",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(example = "{\n  \"nombre\": \"Barra Olímpica\",\n  \"descripcion\": \"Acero 20kg\",\n  \"precio\": 150000,\n  \"stock\": 10,\n  \"imagenUri\": \"barra_fit\",\n  \"categoriaId\": 1\n}")
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado correctamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida (faltan datos)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> crear(
        @RequestBody Map<String, Object> payload, 
        @Parameter(hidden = true) 
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader 
    ) {
        try {
            Producto producto = new Producto();
            producto.setNombre((String) payload.get("nombre"));
            producto.setDescripcion((String) payload.get("descripcion"));
            producto.setPrecio(Double.valueOf(payload.get("precio").toString()));
            
            producto.setStock(Integer.valueOf(payload.get("stock").toString())); 
            producto.setImagenUri((String) payload.get("imagenUri"));

            Categoria categoria = new Categoria();
            categoria.setId(Long.valueOf(payload.get("categoriaId").toString()));
            producto.setCategoria(categoria);

            Producto creado = productoService.guardarProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear producto: " + e.getMessage());
        }
    }

    @Operation(summary = "Editar producto", description = "Modifica los datos de un producto existente (precio, stock, etc.).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
        @Parameter(description = "ID del producto a editar", example = "1") 
        @PathVariable Long id, 
        @RequestBody Map<String, Object> payload,
        @Parameter(hidden = true)
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader 
    ) {
        try {
            return productoService.obtenerPorId(id).map(producto -> {
                producto.setNombre((String) payload.get("nombre"));
                producto.setDescripcion((String) payload.get("descripcion"));
                producto.setPrecio(Double.valueOf(payload.get("precio").toString()));
                producto.setStock(Integer.valueOf(payload.get("stock").toString()));
                producto.setImagenUri((String) payload.get("imagenUri"));

                Categoria categoria = new Categoria();
                categoria.setId(Long.valueOf(payload.get("categoriaId").toString()));
                producto.setCategoria(categoria);

                Producto actualizado = productoService.guardarProducto(producto);
                return ResponseEntity.ok(actualizado);
            }).orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al editar producto: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar producto", description = "Borra un producto del sistema por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del producto a eliminar", example = "1")
        @PathVariable Long id,
        @Parameter(hidden = true)
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader 
    ) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar producto: " + e.getMessage());
        }
    }
}