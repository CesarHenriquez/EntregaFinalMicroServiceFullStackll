package com.microservicio.registrousuario.service;

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

import com.microservicio.registrousuario.model.Rol;
import com.microservicio.registrousuario.repository.RolRepository;

@ExtendWith(MockitoExtension.class)
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol rolAdmin;
    private Rol rolCliente;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol(1L, "ADMINISTRADOR");
        rolCliente = new Rol(2L, "CLIENTE");
    }

    
    @Test
    void testListarRoles_DevuelveListaCompleta() {
        List<Rol> rolesEsperados = Arrays.asList(rolAdmin, rolCliente);
        when(rolRepository.findAll()).thenReturn(rolesEsperados);

        List<Rol> resultado = rolService.listarRoles();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("ADMINISTRADOR", resultado.get(0).getNombre());
        
        verify(rolRepository, times(1)).findAll();
    }

   
    @Test
    void testObtenerPorNombre_Encontrado() {
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        
        Optional<Rol> resultado = rolService.obtenerPorNombre("CLIENTE");
        
        assertTrue(resultado.isPresent());
        assertEquals("CLIENTE", resultado.get().getNombre());
        
        verify(rolRepository, times(1)).findByNombre("CLIENTE");
    }

    
    @Test
    void testObtenerPorId_Encontrado() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));
        
        Optional<Rol> resultado = rolService.obtenerPorId(1L);
        
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        verify(rolRepository, times(1)).findById(1L);
    }

    
    @Test
    void testGuardarRol_GuardaYRetornaRol() {
        Rol nuevoRol = new Rol(null, "DELIVERY");
        Rol rolGuardado = new Rol(3L, "DELIVERY");
        when(rolRepository.save(nuevoRol)).thenReturn(rolGuardado);

        Rol resultado = rolService.guardarRol(nuevoRol);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        
        verify(rolRepository, times(1)).save(nuevoRol);
    }
}
