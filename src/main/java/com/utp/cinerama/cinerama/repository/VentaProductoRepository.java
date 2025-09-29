package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.VentaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaProductoRepository extends JpaRepository<VentaProducto, Long> {
}