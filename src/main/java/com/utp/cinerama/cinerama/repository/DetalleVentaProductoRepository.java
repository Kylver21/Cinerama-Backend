package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.DetalleVentaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaProductoRepository extends JpaRepository<DetalleVentaProducto, Long> {
    List<DetalleVentaProducto> findByVentaProductoId(Long ventaProductoId);
}