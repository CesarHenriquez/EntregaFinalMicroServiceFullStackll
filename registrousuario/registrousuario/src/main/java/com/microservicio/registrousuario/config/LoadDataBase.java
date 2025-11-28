package com.microservicio.registrousuario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservicio.registrousuario.model.Rol;
import com.microservicio.registrousuario.model.Usuario;
import com.microservicio.registrousuario.repository.RolRepository;
import com.microservicio.registrousuario.repository.UsuarioRepository;

@Configuration
public class LoadDataBase {

        @Bean
        CommandLineRunner initDatabase(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder) {
                return args -> {
                        // 1. ASEGURAR Y OBTENER LOS ROLES
                        Rol clienteRol = rolRepository.findByNombre("CLIENTE")
                                        .orElseGet(() -> rolRepository.save(new Rol(null, "CLIENTE")));

                        Rol deliveryRol = rolRepository.findByNombre("DELIVERY")
                                        .orElseGet(() -> rolRepository.save(new Rol(null, "DELIVERY")));

                        Rol adminRol = rolRepository.findByNombre("ADMINISTRADOR")
                                        .orElseGet(() -> rolRepository.save(new Rol(null, "ADMINISTRADOR")));

                        // 2. INSERCIÓN DEL USUARIO ADMINISTRADOR

                        if (usuarioRepository.findByNickname("admin1").isEmpty() && adminRol != null) {

                                String passAdmin = passwordEncoder.encode("12345");

                                Usuario admin = new Usuario(
                                                null,
                                                "admin1",
                                                passAdmin,
                                                "admin@bootstrap.com",
                                                adminRol);
                                usuarioRepository.save(admin);
                                System.out.println(">>> Usuario ADMIN creado: admin1 / 12345");
                        }

                        // Crear Usuario Vendedor
                        if (usuarioRepository.findByNickname("vendedor1").isEmpty() && deliveryRol != null) {
                                String pass = passwordEncoder.encode("12345"); 
                                Usuario vendedor = new Usuario(
                                                null,
                                                "vendedor1",
                                                pass,
                                                "vendedor@fitquality.com", 
                                                deliveryRol 
                                );
                                usuarioRepository.save(vendedor);
                                System.out.println(">>> Usuario VENDEDOR creado: vendedor@fitquality.com / 12345");
                        }
                };
        }
}