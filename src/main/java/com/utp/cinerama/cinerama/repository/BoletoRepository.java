package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    List<Boleto> findByClienteId(Long clienteId);
    List<Boleto> findByFuncionId(Long funcionId);
    List<Boleto> findByEstado(Boleto.EstadoBoleto estado);
}