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
import com.microservicio.direcciones.model.Region;
import com.microservicio.direcciones.service.RegionService;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockBean
    private RegionService regionService;

    private Region region;
    private String regionJson;

    @BeforeEach
    void setUp() throws Exception {
       
        region = new Region();
        region.setId(1L);
        region.setNombre("Metropolitana");
        
       
        this.regionJson = new ObjectMapper().writeValueAsString(region);
    }

    @Test
    void testListarRegiones_RetornaOk() throws Exception {
        List<Region> listaEsperada = Arrays.asList(region);
        when(regionService.listar()).thenReturn(listaEsperada);

        mockMvc.perform(get("/api/regiones")
                .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGuardarRegion_RetornaOk() throws Exception {
        when(regionService.guardar(any(Region.class))).thenReturn(region);

        mockMvc.perform(post("/api/regiones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(regionJson))
                
                .andExpect(status().isOk());
    }
}