package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    List<Sala> findByTipo(Sala.TipoSala tipo);
    List<Sala> findByActivaTrue();
    List<Sala> findByCapacidadGreaterThanEqual(Integer capacidad);
}