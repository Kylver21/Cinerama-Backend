package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByClienteId(Long clienteId);
    List<Pago> findByFechaPago(LocalDate fecha);
    List<Pago> findByEstado(Pago.EstadoPago estado);
}