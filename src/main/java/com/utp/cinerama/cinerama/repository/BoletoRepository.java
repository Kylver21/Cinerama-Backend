package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    @Query("SELECT b FROM Boleto b WHERE b.cliente.id = :clienteId")
    List<Boleto> findByClienteId(@Param("clienteId") Long clienteId);

        @Query("SELECT b FROM Boleto b WHERE b.cliente.id = :clienteId AND b.estado IN :estados")
        List<Boleto> findByClienteIdAndEstadoIn(
            @Param("clienteId") Long clienteId,
            @Param("estados") List<Boleto.EstadoBoleto> estados
        );

    @Query("SELECT b FROM Boleto b WHERE b.funcion.id = :funcionId")
    List<Boleto> findByFuncionId(@Param("funcionId") Long funcionId);

    @Query("SELECT b FROM Boleto b WHERE b.estado = :estado")
    List<Boleto> findByEstado(@Param("estado") Boleto.EstadoBoleto estado);

    /**
     * Elimina todos los boletos de una función específica
     * Usado cuando se elimina una función completa
     */
    @Modifying
    @Query("DELETE FROM Boleto b WHERE b.funcion.id = :funcionId")
    void deleteByFuncionId(@Param("funcionId") Long funcionId);
}