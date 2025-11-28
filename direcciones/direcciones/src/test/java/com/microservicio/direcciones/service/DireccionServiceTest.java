package com.microservicio.direcciones.service;

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

import com.microservicio.direcciones.model.Comuna;
import com.microservicio.direcciones.model.Direccion;
import com.microservicio.direcciones.repository.ComunaRepository;
import com.microservicio.direcciones.repository.DireccionRepository;

@ExtendWith(MockitoExtension.class)
public class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @InjectMocks
    private DireccionService direccionService;

    private Comuna comunaExistente;
    private Direccion direccionCliente;
    private final Long CLIENTE_ID = 100L;
    private final Long COMUNA_ID = 5L;

    @BeforeEach
    void setUp() {
        
        comunaExistente = new Comuna();
        comunaExistente.setId(COMUNA_ID);
        comunaExistente.setNombre("Maipú");
        
       
        direccionCliente = new Direccion();
        direccionCliente.setId(1L);
        direccionCliente.setUsuarioId(CLIENTE_ID);
        direccionCliente.setCalle("Avenida Principal 123");
        direccionCliente.setCodigoPostal("9250000");
        direccionCliente.setComuna(comunaExistente);
    }

   
    @Test
    void testGuardarDireccion_ComunaEncontrada_GuardaExitoso() {
        
        when(comunaRepository.findById(COMUNA_ID)).thenReturn(Optional.of(comunaExistente));
        
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccionCliente);

        
        Direccion direccionAGuardar = new Direccion();
        direccionAGuardar.setUsuarioId(CLIENTE_ID);
        direccionAGuardar.setCalle("Avenida Principal 123");
        
        Comuna cId = new Comuna();
        cId.setId(COMUNA_ID);
        direccionAGuardar.setComuna(cId);

        Direccion resultado = direccionService.guardar(direccionAGuardar);

        assertNotNull(resultado);
       
        assertEquals(COMUNA_ID, resultado.getComuna().getId());
        assertEquals("Maipú", resultado.getComuna().getNombre()); 
        
        verify(comunaRepository, times(1)).findById(COMUNA_ID);
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }
    
    
    @Test
    void testGuardarDireccion_ComunaNoEncontrada_GuardaConComunaNula() {
        final Long COMUNA_INEXISTENTE = 99L;
        
      
        when(comunaRepository.findById(COMUNA_INEXISTENTE)).thenReturn(Optional.empty());

        Direccion direccionAGuardar = new Direccion();
        direccionAGuardar.setUsuarioId(CLIENTE_ID);
        
        Comuna cId = new Comuna();
        cId.setId(COMUNA_INEXISTENTE);
        direccionAGuardar.setComuna(cId);
        
        
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> {
            Direccion d = invocation.getArgument(0);
            assertNull(d.getComuna()); 
            return d;
        });

        Direccion resultado = direccionService.guardar(direccionAGuardar);

        assertNotNull(resultado);
        assertNull(resultado.getComuna());
        
        verify(comunaRepository, times(1)).findById(COMUNA_INEXISTENTE);
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }
    
   
    @Test
    void testListarDirecciones() {
        List<Direccion> listaEsperada = Arrays.asList(direccionCliente);
        when(direccionRepository.findAll()).thenReturn(listaEsperada);

        List<Direccion> resultado = direccionService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(direccionRepository, times(1)).findAll();
    }
    
    
    @Test
    void testBuscarPorUsuario() {
        List<Direccion> listaEsperada = Arrays.asList(direccionCliente);
        when(direccionRepository.findByUsuarioId(CLIENTE_ID)).thenReturn(listaEsperada);

        List<Direccion> resultado = direccionService.buscarPorUsuario(CLIENTE_ID);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(direccionRepository, times(1)).findByUsuarioId(CLIENTE_ID);
    }
}