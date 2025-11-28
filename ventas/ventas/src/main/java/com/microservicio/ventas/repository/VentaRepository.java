package com.microservicio.ventas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.microservicio.ventas.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
     @Query("SELECT v FROM Venta v WHERE v.usuarioId = :usuarioId")
    List<Venta> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT v FROM Venta v WHERE v.direccionId = :direccionId")
    List<Venta> findByDireccionId(@Param("direccionId") Long direccionId);


}
