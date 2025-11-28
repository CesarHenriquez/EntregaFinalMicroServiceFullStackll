package com.microservicio.productostienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.productostienda.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
