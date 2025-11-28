package com.microservicio.registrousuario.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.registrousuario.model.Rol;
import com.microservicio.registrousuario.service.RolService;

@WebMvcTest(RolController.class) 
public class RolControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RolService rolService; 

    private Rol rolAdmin;
    private String rolAdminJson;

    @BeforeEach
    void setUp() throws Exception {
        rolAdmin = new Rol(1L, "ADMINISTRADOR");
        rolAdminJson = objectMapper.writeValueAsString(rolAdmin); 
    }

    
    @Test
    void testCrearRol_RetornaOk() throws Exception {
        when(rolService.guardarRol(any(Rol.class))).thenReturn(rolAdmin);

        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rolAdminJson))
                
                .andExpect(status().isOk()); 
        
        verify(rolService, times(1)).guardarRol(any(Rol.class));
    }

    
    @Test
    void testListarRoles_RetornaOk() throws Exception {
        List<Rol> listaRoles = Arrays.asList(rolAdmin, new Rol(2L, "CLIENTE"));
        when(rolService.listarRoles()).thenReturn(listaRoles);

        mockMvc.perform(get("/roles")
                .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isOk());
        
        verify(rolService, times(1)).listarRoles();
    }
    
    
    @Test
    void testObtenerRol_Encontrado_RetornaOk() throws Exception {
        when(rolService.obtenerPorId(1L)).thenReturn(Optional.of(rolAdmin));

        mockMvc.perform(get("/roles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isOk());
    }
    
    
    
}