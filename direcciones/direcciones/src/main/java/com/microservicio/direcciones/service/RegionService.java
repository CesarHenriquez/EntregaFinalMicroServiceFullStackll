package com.microservicio.direcciones.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.microservicio.direcciones.model.Region;
import com.microservicio.direcciones.repository.RegionRepository;

@Service
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<Region> listar() {
        return regionRepository.findAll();
    }

    public Region guardar(Region region) {
        return regionRepository.save(region);
    }

}
