package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.VentaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaProductoRepository extends JpaRepository<VentaProducto, Long> {

	@Query("SELECT DISTINCT v FROM VentaProducto v " +
			"LEFT JOIN FETCH v.detalles d " +
			"LEFT JOIN FETCH d.producto " +
			"WHERE v.cliente.id = :clienteId AND v.completada = true")
	List<VentaProducto> findComprasCompletadasByClienteId(@Param("clienteId") Long clienteId);
}