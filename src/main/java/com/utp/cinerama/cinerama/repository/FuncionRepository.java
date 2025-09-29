package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    List<Funcion> findByPeliculaId(Long peliculaId);
    List<Funcion> findBySalaId(Long salaId);
    
    // Buscar funciones por fecha (solo la parte de fecha de fechaHora)
    @Query("SELECT f FROM Funcion f WHERE DATE(f.fechaHora) = :fecha")
    List<Funcion> findByFecha(@Param("fecha") LocalDate fecha);
    
    // Buscar funciones entre dos fechas y horas
    List<Funcion> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}