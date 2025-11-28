package com.microservicio.ventas.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.microservicio.ventas.client.DireccionClient;
import com.microservicio.ventas.client.ProductoClient;

import com.microservicio.ventas.dto.ProductoDetalleDTO;
import com.microservicio.ventas.model.DetalleVenta;
import com.microservicio.ventas.model.Producto;

import com.microservicio.ventas.model.Venta;
import com.microservicio.ventas.repository.DetalleVentaRepository;
import com.microservicio.ventas.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoClient productoClient;
    
    private final DireccionClient direccionClient;

    public VentaService(VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            ProductoClient productoClient,
            
            DireccionClient direccionClient) { 
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoClient = productoClient;
       
        this.direccionClient = direccionClient;
    }

    @Transactional
    
    public ResponseEntity<?> registrarVenta(Long usuarioId, String rol, Map<String, Object> payload) {
        try {
            

            // Validar rol CLIENTE (Autorización)
            if (!rol.equalsIgnoreCase("CLIENTE")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo CLIENTES pueden realizar ventas.");
            }

            Venta venta = new Venta();
            venta.setUsuarioId(usuarioId); 

           
            if (!payload.containsKey("direccionId")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta el campo 'direccionId' en el cuerpo de la solicitud.");
            }
            if (!payload.containsKey("detalles")) {
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta el campo 'detalles' en el cuerpo de la solicitud.");
            }
            
            venta.setDireccionId(Long.valueOf(payload.get("direccionId").toString()));
            venta.setFecha(LocalDate.now());

            Venta ventaGuardada = ventaRepository.save(venta);

            List<Map<String, Object>> detalles = (List<Map<String, Object>>) payload.get("detalles");
            List<DetalleVenta> detallesGuardados = new ArrayList<>();

            for (Map<String, Object> detalleMap : detalles) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(ventaGuardada);
                
                Long productoId = Long.valueOf(detalleMap.get("productoId").toString());
                Integer cantidad = Integer.valueOf(detalleMap.get("cantidad").toString());

                
                Producto producto = productoClient.obtenerProductoPorId(productoId).block();
                if (producto == null)
                    continue;

                detalle.setProductoId(productoId);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(producto.getPrecio());

                DetalleVenta detalleGuardado = detalleVentaRepository.save(detalle);
                detallesGuardados.add(detalleGuardado);
            }

            ventaGuardada.setDetalles(detallesGuardados);
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la venta: " + e.getMessage());
        }
    }

    

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public List<DetalleVenta> listarDetalles() {
        return detalleVentaRepository.findAll();
    }

    public List<Venta> listarPorUsuarioId(Long idUsuario) {
        return ventaRepository.findByUsuarioId(idUsuario);
    }

    public List<Venta> listarPorDireccionId(Long direccionId) {
        return ventaRepository.findByDireccionId(direccionId);
    }

    public List<ProductoDetalleDTO> obtenerProductosPorVentaId(Long ventaId) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(ventaId);
        List<ProductoDetalleDTO> productos = new ArrayList<>();

        for (DetalleVenta detalle : detalles) {
            Producto producto = productoClient.obtenerProductoPorId(detalle.getProductoId()).block();
            if (producto != null) {
                ProductoDetalleDTO dto = new ProductoDetalleDTO();
                dto.setProductoId(producto.getId());
                dto.setNombreProducto(producto.getNombre());
                dto.setCantidad(detalle.getCantidad());
                dto.setPrecioUnitario(detalle.getPrecioUnitario());
                productos.add(dto);
            }
        }

        return productos;
    }
   
    @Transactional
    public Venta setProofAndMarkDelivered(Long ventaId, String proofUri) {
        return ventaRepository.findById(ventaId).map(venta -> {
            venta.setProofUri(proofUri);
            venta.setDelivered(true); 
            return ventaRepository.save(venta);
        }).orElse(null);
    }
}