package com.microservicio.direcciones.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.microservicio.direcciones.model.Comuna;
import com.microservicio.direcciones.repository.ComunaRepository;

@Service
public class ComunaService {
    private final ComunaRepository comunaRepository;

    public ComunaService(ComunaRepository comunaRepository) {
        this.comunaRepository = comunaRepository;
    }

    public List<Comuna> listar() {
        return comunaRepository.findAll();
    }

    public List<Comuna> listarPorRegion(Long regionId) {
        return comunaRepository.findByRegionId(regionId);
    }

    public Comuna guardar(Comuna comuna) {
        return comunaRepository.save(comuna);
    }


}
