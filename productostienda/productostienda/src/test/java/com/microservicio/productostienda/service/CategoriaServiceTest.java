package com.microservicio.productostienda.service;

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

import com.microservicio.productostienda.model.Categoria;
import com.microservicio.productostienda.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Calzado Deportivo");
    }

    @Test
    void testListarCategorias() {
        List<Categoria> listaEsperada = Arrays.asList(categoria, new Categoria(2L, "Ropa"));
        when(categoriaRepository.findAll()).thenReturn(listaEsperada);

        List<Categoria> resultado = categoriaService.listarCategorias();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId_Encontrado() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Optional<Categoria> resultado = categoriaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Calzado Deportivo", resultado.get().getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGuardarCategoria() {
        Categoria nuevaCategoria = new Categoria(null, "Accesorios");
        Categoria categoriaGuardada = new Categoria(3L, "Accesorios");
        when(categoriaRepository.save(nuevaCategoria)).thenReturn(categoriaGuardada);

        Categoria resultado = categoriaService.guardarCategoria(nuevaCategoria);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        verify(categoriaRepository, times(1)).save(nuevaCategoria);
    }
}