package com.microservicio.ventas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservicio.ventas.model.DetalleVenta;
import com.microservicio.ventas.model.Venta;
import com.microservicio.ventas.repository.DetalleVentaRepository;

@ExtendWith(MockitoExtension.class)
public class DetalleVentaServiceTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private DetalleVentaService detalleVentaService;

    private DetalleVenta detalleVenta1;
    private Venta ventaMock;
    private final Long DETALLE_ID = 5L;

    @BeforeEach
    void setUp() {
       
        ventaMock = new Venta();
        ventaMock.setId(1L);
        
       
        detalleVenta1 = new DetalleVenta();
        detalleVenta1.setId(DETALLE_ID);
        detalleVenta1.setVenta(ventaMock);
        detalleVenta1.setProductoId(10L);
        detalleVenta1.setCantidad(2);
        detalleVenta1.setPrecioUnitario(50.0);
    }

    
    @Test
    void testListarDetalles_RetornaLista() {
        DetalleVenta detalleVenta2 = new DetalleVenta();
        detalleVenta2.setId(6L);
        
        List<DetalleVenta> listaEsperada = Arrays.asList(detalleVenta1, detalleVenta2);
        when(detalleVentaRepository.findAll()).thenReturn(listaEsperada);

        List<DetalleVenta> resultado = detalleVentaService.listarDetalles();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(detalleVentaRepository, times(1)).findAll();
    }

    
    @Test
    void testObtenerPorId_Encontrado_RetornaObjeto() {
        when(detalleVentaRepository.findById(DETALLE_ID)).thenReturn(Optional.of(detalleVenta1));

        DetalleVenta resultado = detalleVentaService.obtenerPorId(DETALLE_ID);

        assertNotNull(resultado);
        assertEquals(DETALLE_ID, resultado.getId());
        assertEquals(10L, resultado.getProductoId());
        verify(detalleVentaRepository, times(1)).findById(DETALLE_ID);
    }

    
    @Test
    void testObtenerPorId_NoEncontrado_RetornaNull() {
        final Long ID_INEXISTENTE = 99L;
        when(detalleVentaRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

        DetalleVenta resultado = detalleVentaService.obtenerPorId(ID_INEXISTENTE);

        assertNull(resultado);
        verify(detalleVentaRepository, times(1)).findById(ID_INEXISTENTE);
    }
}
