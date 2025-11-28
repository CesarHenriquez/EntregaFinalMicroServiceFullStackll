package com.microservicio.autenticarusuario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservicio.autenticarusuario.client.UsuarioClient;
import com.microservicio.autenticarusuario.dto.LoginResponseDTO; // ⬅️ IMPORTACIÓN CLAVE
import com.microservicio.autenticarusuario.model.Rol;
import com.microservicio.autenticarusuario.model.Usuario;
import com.microservicio.autenticarusuario.util.JwtUtil;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioCliente;
    private final String EMAIL = "cliente@mail.com"; // Usaremos EMAIL en lugar de NICKNAME
    private final String CLAVE_SIN_ENCRIPTAR = "clave123";
    private final String CLAVE_ENCRIPTADA = "$2a$10$encodedhash";
    private final String TOKEN_GENERADO = "mock.jwt.token";

    @BeforeEach
    void setUp() {
        // Corrección: Usamos 'correo' para la prueba consistente con la implementación
        usuarioCliente = new Usuario(1L, "cliente1", CLAVE_ENCRIPTADA, EMAIL, new Rol(1L, "CLIENTE"));
    }

    // ⬇️ TEST CORREGIDO: Espera el objeto LoginResponseDTO ⬇️
    @Test
    void testAutenticar_CredencialesCorrectas_EmiteLoginResponseDTO() {
        // Simula la obtención del usuario por email
        when(usuarioClient.obtenerUsuarioPorEmail(EMAIL)).thenReturn(Mono.just(usuarioCliente));

        // Simula que la contraseña coincide
        when(passwordEncoder.matches(CLAVE_SIN_ENCRIPTAR, CLAVE_ENCRIPTADA)).thenReturn(true);

        // Simula la generación del token
        when(jwtUtil.generateToken(usuarioCliente)).thenReturn(TOKEN_GENERADO);

        Mono<Object> resultadoMono = authService.autenticar(EMAIL, CLAVE_SIN_ENCRIPTAR);

        StepVerifier.create(resultadoMono)
                .expectNextMatches(result -> 
                    // ⬇️ Verifica que el resultado sea el DTO esperado ⬇️
                    result instanceof LoginResponseDTO &&
                    ((LoginResponseDTO) result).getToken().equals(TOKEN_GENERADO) &&
                    ((LoginResponseDTO) result).getUser().getCorreo().equals(EMAIL)
                )
                .verifyComplete();

        verify(usuarioClient, times(1)).obtenerUsuarioPorEmail(EMAIL);
        verify(jwtUtil, times(1)).generateToken(usuarioCliente);
    }
    
    // ⬇️ TEST CORREGIDO: Espera el mensaje de error si el usuario no existe ⬇️
    @Test
    void testAutenticar_UsuarioNoEncontrado_EmiteMensajeDeError() {
        // Simula que no encuentra el usuario (Mono.empty())
        when(usuarioClient.obtenerUsuarioPorEmail(anyString())).thenReturn(Mono.empty());

        Mono<Object> resultadoMono = authService.autenticar("unknown@mail.com", CLAVE_SIN_ENCRIPTAR);

        StepVerifier.create(resultadoMono)
                .expectNext("El email no está registrado.")
                .verifyComplete();

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ⬇️ TEST CORREGIDO: Espera el mensaje de error si la clave es incorrecta ⬇️
    @Test
    void testAutenticar_ClaveIncorrecta_EmiteMensajeDeError() {
        when(usuarioClient.obtenerUsuarioPorEmail(EMAIL)).thenReturn(Mono.just(usuarioCliente));

        // Simula que la contraseña NO coincide
        when(passwordEncoder.matches(CLAVE_SIN_ENCRIPTAR, CLAVE_ENCRIPTADA)).thenReturn(false);

        Mono<Object> resultadoMono = authService.autenticar(EMAIL, CLAVE_SIN_ENCRIPTAR);

        StepVerifier.create(resultadoMono)
                .expectNext("Contraseña incorrecta.")
                .verifyComplete();

        verify(jwtUtil, never()).generateToken(any(Usuario.class));
    }
    
    // El test de error de comunicación sigue siendo válido, ya que devuelve String.
    @Test
    void testAutenticar_ErrorDeComunicacion_EmiteMensajeDeError() {
        when(usuarioClient.obtenerUsuarioPorEmail(EMAIL)).thenReturn(Mono.error(new RuntimeException("Error de red")));

        Mono<Object> resultadoMono = authService.autenticar(EMAIL, CLAVE_SIN_ENCRIPTAR);

        StepVerifier.create(resultadoMono)
                .expectNextMatches(msg -> msg.toString().startsWith("Error al autenticar:"))
                .verifyComplete();
    }
}