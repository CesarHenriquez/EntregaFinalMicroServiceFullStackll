package com.microservicio.direcciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.direcciones.model.Comuna;

public interface ComunaRepository extends JpaRepository<Comuna, Long> {
     List<Comuna> findByRegionId(Long regionId);

}
