package com.microservicio.registrousuario.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.registrousuario.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
     Optional<Rol> findByNombre(String nombre); 
}
