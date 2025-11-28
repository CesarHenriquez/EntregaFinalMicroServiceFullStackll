package com.microservicio.direcciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservicio.direcciones.model.Comuna;
import com.microservicio.direcciones.model.Region;
import com.microservicio.direcciones.repository.ComunaRepository;

@ExtendWith(MockitoExtension.class)
public class ComunaServiceTest {

    @Mock
    private ComunaRepository comunaRepository;

    @InjectMocks
    private ComunaService comunaService;

    private Comuna comuna;
    private Region region;
    private final Long REGION_ID = 1L;

    @BeforeEach
    void setUp() {
       
        region = new Region();
        region.setId(REGION_ID);
        region.setNombre("Región Metropolitana");
        
        
        comuna = new Comuna();
        comuna.setId(10L);
        comuna.setNombre("Santiago");
        comuna.setRegion(region);
    }

    
    @Test
    void testListarComunas() {
        
        Comuna c2 = new Comuna();
        c2.setId(11L);
        c2.setNombre("Providencia");
        c2.setRegion(region);
        
        List<Comuna> listaEsperada = Arrays.asList(comuna, c2);
        when(comunaRepository.findAll()).thenReturn(listaEsperada);

        List<Comuna> resultado = comunaService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(comunaRepository, times(1)).findAll();
    }

    
    @Test
    void testListarPorRegion() {
        List<Comuna> listaEsperada = Arrays.asList(comuna);
        when(comunaRepository.findByRegionId(REGION_ID)).thenReturn(listaEsperada);

        List<Comuna> resultado = comunaService.listarPorRegion(REGION_ID);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Santiago", resultado.get(0).getNombre());
        verify(comunaRepository, times(1)).findByRegionId(REGION_ID);
    }
    
   
    @Test
    void testGuardarComuna() {
        
        Comuna nuevaComuna = new Comuna();
        nuevaComuna.setNombre("Puente Alto");
        
        
        Comuna comunaGuardada = new Comuna();
        comunaGuardada.setId(15L);
        comunaGuardada.setNombre("Puente Alto");
        
        when(comunaRepository.save(any(Comuna.class))).thenReturn(comunaGuardada);

        Comuna resultado = comunaService.guardar(nuevaComuna);

        assertNotNull(resultado);
        assertEquals(15L, resultado.getId());
        verify(comunaRepository, times(1)).save(any(Comuna.class));
    }
}