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

import com.microservicio.direcciones.model.Region;
import com.microservicio.direcciones.repository.RegionRepository;

@ExtendWith(MockitoExtension.class)
public class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    private Region region; 

    @BeforeEach
    void setUp() {
        
        region = new Region();
        region.setId(1L);
        region.setNombre("Metropolitana");
    }

    @Test
    void testListarRegiones() {
       
        Region r2 = new Region();
        r2.setId(2L);
        r2.setNombre("Valparaíso");
        
        List<Region> listaEsperada = Arrays.asList(region, r2);
        when(regionRepository.findAll()).thenReturn(listaEsperada);

        List<Region> resultado = regionService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Valparaíso", resultado.get(1).getNombre());
        verify(regionRepository, times(1)).findAll();
    }

    @Test
    void testGuardarRegion() {
       
        Region nuevaRegion = new Region();
        nuevaRegion.setNombre("Biobío");
        
        
        Region regionGuardada = new Region();
        regionGuardada.setId(3L);
        regionGuardada.setNombre("Biobío");
        
        when(regionRepository.save(any(Region.class))).thenReturn(regionGuardada);

        Region resultado = regionService.guardar(nuevaRegion);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        verify(regionRepository, times(1)).save(any(Region.class));
    }
}