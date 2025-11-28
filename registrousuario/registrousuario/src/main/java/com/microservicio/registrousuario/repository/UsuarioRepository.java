package com.microservicio.registrousuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.registrousuario.model.Usuario;

public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
     Optional<Usuario> findByNickname(String nickname);
     Optional<Usuario> findByCorreo(String correo);

}
