package com.microservicio.autenticarusuario.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservicio.autenticarusuario.client.UsuarioClient;
import com.microservicio.autenticarusuario.dto.LoginResponseDTO;
import com.microservicio.autenticarusuario.model.Usuario;
import com.microservicio.autenticarusuario.util.JwtUtil;

import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private final UsuarioClient usuarioClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioClient usuarioClient, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioClient = usuarioClient;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<Object> autenticar(String email, String clave) {
       
        System.out.println(">>> [DEBUG] 1. Intento de login para: " + email);
        System.out.println(">>> [DEBUG] 1. Clave recibida (raw): '" + clave + "'");

        return usuarioClient.obtenerUsuarioPorEmail(email)
                .doOnNext(u -> System.out.println(">>> [DEBUG] 2. Usuario encontrado en MS 8020: " + u.getNickname()))
                .flatMap(usuario -> {

                    
                    String hashEnBd = usuario.getClave();
                    System.out.println(">>> [DEBUG] 3. Hash recuperado de la BD: '" + hashEnBd + "'");

                  
                    boolean coincide = passwordEncoder.matches(clave, hashEnBd);
                    System.out.println(">>> [DEBUG] 4. Resultado de passwordEncoder.matches(): " + coincide);

                    if (coincide) {
                       
                        String token = jwtUtil.generateToken(usuario);
                        System.out.println(">>> [DEBUG] 5. Login EXITOSO. Token generado.");
                        return Mono.just(new LoginResponseDTO(token, usuario));
                    } else {
                        System.out.println(">>> [DEBUG] 5. Login FALLIDO. Contraseña no coincide.");
                       
                        System.out.println(">>> [DEBUG]    (Referencia) La clave '" + clave + "' generaría este hash: "
                                + passwordEncoder.encode(clave));
                        return Mono.just("Contraseña incorrecta.");
                    }
                })
                .defaultIfEmpty("El email no está registrado.")
                .doOnSuccess(result -> {
                    if (result instanceof String && result.equals("El email no está registrado.")) {
                        System.out
                                .println(">>> [DEBUG] Error: El usuarioclient devolvió vacío (usuario no encontrado).");
                    }
                })
                .onErrorResume(e -> {
                    System.out.println(">>> [DEBUG] EXCEPCIÓN CRÍTICA en AuthService: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just("Error al autenticar: " + e.getMessage());
                });
    }

    public Mono<Usuario> findUserByEmail(String email) {
        return usuarioClient.obtenerUsuarioPorEmail(email);
    }
}
