package com.microservicio.ventas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.microservicio.ventas.model.DetalleVenta;
import com.microservicio.ventas.repository.DetalleVentaRepository;

@Service
public class DetalleVentaService {
    private final DetalleVentaRepository detalleVentaRepository;

    public DetalleVentaService(DetalleVentaRepository detalleVentaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
    }

    public List<DetalleVenta> listarDetalles() {
        return detalleVentaRepository.findAll();
    }

    public DetalleVenta obtenerPorId(Long id) {
        return detalleVentaRepository.findById(id).orElse(null);
    }


}
