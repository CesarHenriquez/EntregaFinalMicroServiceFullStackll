package com.microservicio.direcciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.direcciones.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

}
