package com.microservicio.direcciones.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.direcciones.model.Comuna;
import com.microservicio.direcciones.model.Region;
import com.microservicio.direcciones.service.ComunaService;

@WebMvcTest(ComunaController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class ComunaControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ComunaService comunaService;

    private Comuna comuna;
    private String comunaJson;
    private final Long REGION_ID = 1L;

    @BeforeEach
    void setUp() throws Exception {
       
        Region region = new Region();
        region.setId(REGION_ID);
        
        comuna = new Comuna();
        comuna.setId(10L);
        comuna.setNombre("Santiago");
        comuna.setRegion(region);
        
        comunaJson = objectMapper.writeValueAsString(comuna);
    }

    
    @Test
    void testListarComunas_RetornaOk() throws Exception {
        List<Comuna> listaEsperada = Arrays.asList(comuna);
        when(comunaService.listar()).thenReturn(listaEsperada);

        mockMvc.perform(get("/api/comunas")
                .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Santiago"));
    }

    
    @Test
    void testListarPorRegion_RetornaOk() throws Exception {
        List<Comuna> listaEsperada = Arrays.asList(comuna);
        when(comunaService.listarPorRegion(REGION_ID)).thenReturn(listaEsperada);

        mockMvc.perform(get("/api/comunas/region/{regionId}", REGION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    
    @Test
    void testGuardarComuna_RetornaOk() throws Exception {
        when(comunaService.guardar(any(Comuna.class))).thenReturn(comuna);

        mockMvc.perform(post("/api/comunas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(comunaJson))
                
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.nombre").value("Santiago"));
        
        verify(comunaService, times(1)).guardar(any(Comuna.class));
    }
}