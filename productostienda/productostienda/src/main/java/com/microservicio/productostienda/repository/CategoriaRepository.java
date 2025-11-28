package com.microservicio.productostienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.productostienda.model.Categoria;
import java.util.Optional; 

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
     
     Optional<Categoria> findByNombre(String nombre);
}