package com.microservicio.direcciones.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.direcciones.model.Comuna;
import com.microservicio.direcciones.model.Direccion;
import com.microservicio.direcciones.service.DireccionService;
import com.microservicio.direcciones.util.JwtUtil;

@WebMvcTest(DireccionController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class DireccionControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DireccionService direccionService;

    @MockBean
    private JwtUtil jwtUtil; 

    private final String CLIENTE_TOKEN = "Bearer client_token";
    private final String ADMIN_TOKEN = "Bearer admin_token";
    private final String INVALID_TOKEN = "Bearer invalid_token";
    private final Long CLIENTE_ID = 50L;
    private final Long COMUNA_ID = 5L;
    
    private Direccion direccion;
    private String direccionPayloadJson;

    @BeforeEach
    void setUp() throws Exception {
       
        Comuna comuna = new Comuna();
        comuna.setId(COMUNA_ID);
        
        direccion = new Direccion();
        direccion.setId(1L);
        direccion.setUsuarioId(CLIENTE_ID);
        direccion.setCalle("Av. Siempre Viva 742");
        direccion.setCodigoPostal("9250000");
        direccion.setComuna(comuna);

       
        Map<String, Object> payload = new HashMap<>();
        payload.put("calle", "Av. Siempre Viva 742");
        payload.put("codigoPostal", "9250000");
        payload.put("comunaId", COMUNA_ID);
        direccionPayloadJson = objectMapper.writeValueAsString(payload);
        
       
        when(jwtUtil.extractUserId(CLIENTE_TOKEN)).thenReturn(CLIENTE_ID);
        when(jwtUtil.extractRole(CLIENTE_TOKEN)).thenReturn("CLIENTE");
        
        when(jwtUtil.extractUserId(ADMIN_TOKEN)).thenReturn(10L); 
        when(jwtUtil.extractRole(ADMIN_TOKEN)).thenReturn("ADMINISTRADOR");
        
        when(jwtUtil.extractUserId(INVALID_TOKEN)).thenReturn(null);
        when(jwtUtil.extractRole(INVALID_TOKEN)).thenReturn(null);
    }

   

    @Test
    void testListarDirecciones_RetornaOk() throws Exception {
        when(direccionService.listar()).thenReturn(Arrays.asList(direccion));

        mockMvc.perform(get("/api/direcciones")
                .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
    
    @Test
    void testPorUsuario_RetornaDirecciones() throws Exception {
        when(direccionService.buscarPorUsuario(CLIENTE_ID)).thenReturn(Arrays.asList(direccion));

        mockMvc.perform(get("/api/direcciones/usuario/{usuarioId}", CLIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
    
   
    
    @Test
    void testGuardarDireccion_ComoCliente_RetornaCreated() throws Exception {
        when(direccionService.guardar(any(Direccion.class))).thenReturn(direccion);

        mockMvc.perform(post("/api/direcciones")
                .header("Authorization", CLIENTE_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(direccionPayloadJson))
                
                .andExpect(status().isCreated());
        
       
        verify(direccionService, times(1)).guardar(argThat(d -> d.getUsuarioId().equals(CLIENTE_ID)));
    }

    @Test
    void testGuardarDireccion_ComoAdmin_RetornaForbidden() throws Exception {
        mockMvc.perform(post("/api/direcciones")
                .header("Authorization", ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(direccionPayloadJson))
                
                .andExpect(status().isForbidden());
        
        verify(direccionService, never()).guardar(any(Direccion.class));
    }
    
    @Test
    void testGuardarDireccion_TokenInvalido_RetornaUnauthorized() throws Exception {
        mockMvc.perform(post("/api/direcciones")
                .header("Authorization", INVALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(direccionPayloadJson))
                
                .andExpect(status().isUnauthorized());
        
        verify(direccionService, never()).guardar(any(Direccion.class));
    }
    
    @Test
    void testGuardarDireccion_FaltaComunaId_RetornaBadRequest() throws Exception {
       
        Map<String, Object> invalidPayload = new HashMap<>();
        invalidPayload.put("calle", "Test");
        String invalidJson = objectMapper.writeValueAsString(invalidPayload);
        
        mockMvc.perform(post("/api/direcciones")
                .header("Authorization", CLIENTE_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Falta el campo 'comunaId'")));
        
        verify(direccionService, never()).guardar(any(Direccion.class));
    }
}