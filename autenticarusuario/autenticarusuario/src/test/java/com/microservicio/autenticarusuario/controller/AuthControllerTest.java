package com.microservicio.autenticarusuario.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.microservicio.autenticarusuario.dto.LoginResponseDTO; // ⬅️ IMPORTACIÓN CLAVE
import com.microservicio.autenticarusuario.model.Rol;
import com.microservicio.autenticarusuario.model.Usuario;
import com.microservicio.autenticarusuario.service.AuthService;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class) 
public class AuthControllerTest {

    @Mock 
    private AuthService authService; 

    @InjectMocks 
    private AuthController authController; 

    private final String EMAIL = "test@user.com"; // Usaremos EMAIL
    private final String CLAVE = "password123";
    private final String TOKEN_MOCK = "generated.mock.token.12345";
    private Map<String, String> credentials;
    private LoginResponseDTO successDTO;

    @BeforeEach
    void setUp() {
        // El cliente (Kotlin) envía 'correo' y 'clave'
        credentials = Map.of("correo", EMAIL, "clave", CLAVE);
        
        Usuario mockUser = new Usuario(1L, "testuser", null, EMAIL, new Rol(1L, "CLIENTE"));
        successDTO = new LoginResponseDTO(TOKEN_MOCK, mockUser);
    }

    // ⬇️ TEST CORREGIDO: Espera el objeto LoginResponseDTO y status 200 OK ⬇️
    @Test
    void testLogin_Exitoso_RetornaTokenYStatusOk() {
        
        // Simula que el servicio devuelve el DTO de éxito
        when(authService.autenticar(EMAIL, CLAVE)).thenReturn(Mono.just(successDTO));

        // El controlador ahora devuelve un ResponseEntity<?> (con LoginResponseDTO dentro)
        Mono<ResponseEntity<?>> responseMono = authController.login(credentials);
        
        // Obtenemos la respuesta
        ResponseEntity<?> response = responseMono.block();

        // ⬇️ ASUNCIONES CRÍTICAS ⬇️
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponseDTO); // Verifica el tipo de cuerpo
        assertEquals(TOKEN_MOCK, ((LoginResponseDTO) response.getBody()).getToken()); // Verifica el token
        
        verify(authService, times(1)).autenticar(EMAIL, CLAVE);
    }

    // ⬇️ TEST CORREGIDO: Espera el mensaje de error y status 401 UNAUTHORIZED ⬇️
    @Test
    void testLogin_ClaveIncorrecta_RetornaUnauthorized() {
        final String ERROR_MESSAGE = "Contraseña incorrecta.";
        
        // Simula que el servicio devuelve el mensaje de error (String)
        when(authService.autenticar(EMAIL, CLAVE)).thenReturn(Mono.just(ERROR_MESSAGE));

        Mono<ResponseEntity<?>> responseMono = authController.login(credentials);
        ResponseEntity<?> response = responseMono.block();

        // ⬇️ ASUNCIONES CRÍTICAS ⬇️
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()); // 401
        assertTrue(response.getBody() instanceof String); // Verifica que el cuerpo sea String
        assertEquals(ERROR_MESSAGE, response.getBody());
        
        verify(authService, times(1)).autenticar(EMAIL, CLAVE);
    }

    @Test
    void testLogin_UsuarioNoEncontrado_RetornaUnauthorized() {
        final String ERROR_MESSAGE = "El email no está registrado.";
        
        // Simula que el servicio devuelve el mensaje de error (String)
        when(authService.autenticar(EMAIL, CLAVE)).thenReturn(Mono.just(ERROR_MESSAGE));

        Mono<ResponseEntity<?>> responseMono = authController.login(credentials);
        ResponseEntity<?> response = responseMono.block();

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()); // 401
        assertTrue(response.getBody() instanceof String);
        assertEquals(ERROR_MESSAGE, response.getBody());
        
        verify(authService, times(1)).autenticar(EMAIL, CLAVE);
    }
}