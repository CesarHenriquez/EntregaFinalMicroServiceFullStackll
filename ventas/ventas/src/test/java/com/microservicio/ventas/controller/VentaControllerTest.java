package com.microservicio.ventas.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.ventas.model.Venta;
import com.microservicio.ventas.service.VentaService;
import com.microservicio.ventas.util.JwtUtil;

@WebMvcTest(VentaController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockBean
    private VentaService ventaService;

    @MockBean
    private JwtUtil jwtUtil; 

    private final String CLIENTE_TOKEN = "Bearer client_token";
    private final String ADMIN_TOKEN = "Bearer admin_token";
    private final String DELIVERY_TOKEN = "Bearer delivery_token";
    private final String INVALID_TOKEN = "Bearer invalid_token";
    private final Long CLIENTE_ID = 100L;
    private final Long OTRO_ID = 101L;
    
    private String ventaPayloadJson;
    private Venta venta;

    @BeforeEach
    void setUp() throws Exception {
        
        venta = new Venta();
        venta.setId(1L);
        venta.setUsuarioId(CLIENTE_ID);

       
        Map<String, Object> payload = new HashMap<>();
        payload.put("direccionId", 5L);
        payload.put("detalles", List.of(Map.of("productoId", 1, "cantidad", 2)));
        ventaPayloadJson = new ObjectMapper().writeValueAsString(payload);
        
       
        when(jwtUtil.extractUserId(CLIENTE_TOKEN)).thenReturn(CLIENTE_ID);
        when(jwtUtil.extractRole(CLIENTE_TOKEN)).thenReturn("CLIENTE");
        
        when(jwtUtil.extractUserId(ADMIN_TOKEN)).thenReturn(CLIENTE_ID);
        when(jwtUtil.extractRole(ADMIN_TOKEN)).thenReturn("ADMINISTRADOR");
        
        when(jwtUtil.extractUserId(DELIVERY_TOKEN)).thenReturn(CLIENTE_ID);
        when(jwtUtil.extractRole(DELIVERY_TOKEN)).thenReturn("DELIVERY");
        
        when(jwtUtil.extractUserId(INVALID_TOKEN)).thenReturn(null);
        when(jwtUtil.extractRole(INVALID_TOKEN)).thenReturn(null);
        
        
        doReturn(ResponseEntity.status(HttpStatus.CREATED).body(venta))
             .when(ventaService)
             .registrarVenta(eq(CLIENTE_ID), eq("CLIENTE"), anyMap());
             
       
        doReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo CLIENTES pueden realizar ventas."))
             .when(ventaService)
             .registrarVenta(eq(CLIENTE_ID), eq("ADMINISTRADOR"), anyMap());
    }

   

    @Test
    void testRegistrarVenta_ComoCliente_RetornaCreated() throws Exception {
        mockMvc.perform(post("/api/ventas")
                .header("Authorization", CLIENTE_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaPayloadJson))
                
                .andExpect(status().isCreated());
        
        verify(ventaService, times(1)).registrarVenta(eq(CLIENTE_ID), eq("CLIENTE"), anyMap());
    }

    @Test
    void testRegistrarVenta_TokenInvalido_RetornaUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ventas")
                .header("Authorization", INVALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaPayloadJson))
                
                .andExpect(status().isUnauthorized());
        
        verify(ventaService, never()).registrarVenta(anyLong(), anyString(), anyMap());
    }

    @Test
    void testRegistrarVenta_ComoAdmin_RetornaForbiddenDesdeService() throws Exception {
        mockMvc.perform(post("/api/ventas")
                .header("Authorization", ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaPayloadJson))
                
                .andExpect(status().isForbidden());
        
        verify(ventaService, times(1)).registrarVenta(eq(CLIENTE_ID), eq("ADMINISTRADOR"), anyMap());
    }

   

    @Test
    void testListarPorUsuario_AccesoPropio_RetornaOk() throws Exception {
        when(ventaService.listarPorUsuarioId(CLIENTE_ID)).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas/usuario/{id}", CLIENTE_ID)
                .header("Authorization", CLIENTE_TOKEN))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
    
    @Test
    void testListarPorUsuario_AccesoAdminAOtroUsuario_RetornaOk() throws Exception {
        
        when(ventaService.listarPorUsuarioId(OTRO_ID)).thenReturn(List.of(new Venta()));

        mockMvc.perform(get("/api/ventas/usuario/{id}", OTRO_ID)
                .header("Authorization", ADMIN_TOKEN))
                
                .andExpect(status().isOk());
    }

    @Test
    void testListarPorUsuario_AccesoClienteAOtroUsuario_RetornaForbidden() throws Exception {
        
        
        mockMvc.perform(get("/api/ventas/usuario/{id}", OTRO_ID)
                .header("Authorization", CLIENTE_TOKEN))
                
                .andExpect(status().isForbidden());
    }
}