package com.microservicio.productostienda.controller;

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
import com.microservicio.productostienda.model.Categoria;
import com.microservicio.productostienda.service.CategoriaService;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    private Categoria categoria;
    private String categoriaJson;

    @BeforeEach
    void setUp() throws Exception {
        categoria = new Categoria(1L, "Calzado Deportivo");
        categoriaJson = objectMapper.writeValueAsString(categoria);
    }

   
    @Test
    void testListarCategorias_RetornaOk() throws Exception {
        List<Categoria> listaEsperada = Arrays.asList(categoria);
        when(categoriaService.listarCategorias()).thenReturn(listaEsperada);

        mockMvc.perform(get("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                
                
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.size()").value(1));
        
        verify(categoriaService, times(1)).listarCategorias();
    }

   
    @Test
    void testCrearCategoria_RetornaOk() throws Exception {
        when(categoriaService.guardarCategoria(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoriaJson))
                
               
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.nombre").value("Calzado Deportivo"));
        
        verify(categoriaService, times(1)).guardarCategoria(any(Categoria.class));
    }
}