package com.microservicio.ventas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.microservicio.ventas.client.DireccionClient;
import com.microservicio.ventas.client.ProductoClient;
import com.microservicio.ventas.model.DetalleVenta;
import com.microservicio.ventas.model.Producto;
import com.microservicio.ventas.model.Venta;
import com.microservicio.ventas.repository.DetalleVentaRepository;
import com.microservicio.ventas.repository.VentaRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private ProductoClient productoClient; 
    
    @Mock
    private DireccionClient direccionClient; 

    @InjectMocks
    private VentaService ventaService;

    private final Long CLIENTE_ID = 100L;
    private final Long DIRECCION_ID = 5L;
    private final Long PRODUCTO_ID = 1L;
    private Map<String, Object> payload;
    private Venta ventaGuardada;
    private Producto productoMock;
    
    @BeforeEach
    void setUp() {
      
        productoMock = new Producto();
        productoMock.setId(PRODUCTO_ID);
        productoMock.setPrecio(50.0);
        
       
        ventaGuardada = new Venta();
        ventaGuardada.setId(10L);
        ventaGuardada.setUsuarioId(CLIENTE_ID);
        ventaGuardada.setDireccionId(DIRECCION_ID);
        ventaGuardada.setFecha(LocalDate.now());

       
        Map<String, Object> detalle1 = new HashMap<>();
        detalle1.put("productoId", PRODUCTO_ID);
        detalle1.put("cantidad", 2);
        
        payload = new HashMap<>();
        payload.put("direccionId", DIRECCION_ID);
        payload.put("detalles", List.of(detalle1));
    }

    
    @Test
    void testRegistrarVenta_RolCliente_Exito() {
       
        when(productoClient.obtenerProductoPorId(PRODUCTO_ID)).thenReturn(Mono.just(productoMock));
        
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaGuardada);
       
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenAnswer(invocation -> {
            DetalleVenta dv = invocation.getArgument(0);
            dv.setId(1L);
            return dv;
        });

        ResponseEntity<?> response = ventaService.registrarVenta(CLIENTE_ID, "CLIENTE", payload);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
       
        verify(ventaRepository, times(1)).save(any(Venta.class));
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
        verify(productoClient, times(1)).obtenerProductoPorId(PRODUCTO_ID);
    }
    
    
    @Test
    void testRegistrarVenta_RolNoCliente_RetornaForbidden() {
        ResponseEntity<?> response = ventaService.registrarVenta(CLIENTE_ID, "ADMINISTRADOR", payload);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Solo CLIENTES pueden realizar ventas."));
        
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    
    @Test
    void testRegistrarVenta_FaltaDireccionId_RetornaBadRequest() {
        payload.remove("direccionId");

        ResponseEntity<?> response = ventaService.registrarVenta(CLIENTE_ID, "CLIENTE", payload);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Falta el campo 'direccionId'"));
    }

    
    @Test
    void testListarPorUsuarioId() {
        List<Venta> listaEsperada = List.of(ventaGuardada);
        when(ventaRepository.findByUsuarioId(CLIENTE_ID)).thenReturn(listaEsperada);

        List<Venta> resultado = ventaService.listarPorUsuarioId(CLIENTE_ID);

        assertFalse(resultado.isEmpty());
        verify(ventaRepository, times(1)).findByUsuarioId(CLIENTE_ID);
    }
}