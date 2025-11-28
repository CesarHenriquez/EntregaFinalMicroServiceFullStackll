package com.microservicio.registrousuario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.registrousuario.model.Rol;
import com.microservicio.registrousuario.model.Usuario;
import com.microservicio.registrousuario.service.UsuarioService;
import com.microservicio.registrousuario.util.JwtUtil;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; 

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.MediaType;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtUtil jwtUtil; 

    
    private final String ADMIN_TOKEN = "Bearer valid_admin_token";
    private final String DELIVERY_TOKEN = "Bearer valid_delivery_token";
    private final String CLIENTE_TOKEN = "Bearer valid_cliente_token";
    
    private Usuario usuarioAdmin;
    private Usuario usuarioDelivery;
    private String usuarioJson;

    @BeforeEach
    void setUp() throws Exception {
        usuarioAdmin = new Usuario(1L, "adminuser", "pass", "admin@mail.com", new Rol(2L, "ADMINISTRADOR"));
        usuarioDelivery = new Usuario(2L, "deliveryuser", "pass", "delivery@mail.com", new Rol(3L, "DELIVERY"));
        
       
        when(jwtUtil.extractRole(ADMIN_TOKEN)).thenReturn("ADMINISTRADOR");
        when(jwtUtil.extractRole(DELIVERY_TOKEN)).thenReturn("DELIVERY");
        when(jwtUtil.extractRole(CLIENTE_TOKEN)).thenReturn("CLIENTE");
        
        
        when(jwtUtil.extractRole(eq("Bearer invalid_token"))).thenReturn(null);
        
        usuarioJson = objectMapper.writeValueAsString(usuarioAdmin); 
    }

    
    @Test
    void testBuscarPorNicknameInterno_RetornaOk() throws Exception {
        when(usuarioService.buscarPorNickname("adminuser")).thenReturn(usuarioAdmin);
        
        
        mockMvc.perform(get("/usuarios/interno/nickname/adminuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("adminuser"));
    }

   
    @Test
    void testRegistrar_ComoAdmin_RetornaCreated() throws Exception {
        
        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuarioAdmin);

        mockMvc.perform(post("/usuarios")
                .header("Authorization", ADMIN_TOKEN) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                
                
                .andExpect(status().isCreated());
        
        verify(usuarioService, times(1)).crearUsuario(any(Usuario.class));
    }

    @Test
    void testRegistrar_ComoCliente_RetornaForbidden() throws Exception {
        mockMvc.perform(post("/usuarios")
                .header("Authorization", CLIENTE_TOKEN) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                
                
                .andExpect(status().isForbidden());
        
        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }
    
   
    @Test
    void testListar_ComoAdmin_RetornaOk() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(Arrays.asList(usuarioAdmin));

        mockMvc.perform(get("/usuarios")
                .header("Authorization", ADMIN_TOKEN) 
                .contentType(MediaType.APPLICATION_JSON))
                
               
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testListar_ComoDelivery_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/usuarios")
                .header("Authorization", DELIVERY_TOKEN) 
                .contentType(MediaType.APPLICATION_JSON))
                
                
                .andExpect(status().isForbidden());
    }

   
    @Test
    void testObtenerPorId_ComoDelivery_RetornaOk() throws Exception {
        when(usuarioService.obtenerPorId(2L)).thenReturn(Optional.of(usuarioDelivery));

        mockMvc.perform(get("/usuarios/{id}", 2L)
                .header("Authorization", DELIVERY_TOKEN)) 
                
                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("deliveryuser"));
    }
    
    @Test
    void testObtenerPorId_ComoCliente_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/usuarios/{id}", 1L)
                .header("Authorization", CLIENTE_TOKEN)) 
                
                
                .andExpect(status().isForbidden());
    }
    
   
    @Test
    void testActualizar_ComoAdmin_RetornaOk() throws Exception {
        when(usuarioService.actualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuarioAdmin);
        
        mockMvc.perform(put("/usuarios/{id}", 1L)
                .header("Authorization", ADMIN_TOKEN) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson))
                
                
                .andExpect(status().isOk());
    }

}