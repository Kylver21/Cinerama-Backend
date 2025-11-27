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
    @Query("SELECT f FROM Funcion f WHERE f.pelicula.id = :peliculaId")
    List<Funcion> findByPeliculaId(@Param("peliculaId") Long peliculaId);

    @Query("SELECT f FROM Funcion f WHERE f.sala.id = :salaId")
    List<Funcion> findBySalaId(@Param("salaId") Long salaId);
    
    // Buscar funciones por fecha (solo la parte de fecha de fechaHora)
    @Query("SELECT f FROM Funcion f WHERE DATE(f.fechaHora) = :fecha")
    List<Funcion> findByFecha(@Param("fecha") LocalDate fecha);
    
    // Buscar funciones entre dos fechas y horas
    List<Funcion> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Buscar funciones futuras (disponibles para compra)
    @Query("SELECT f FROM Funcion f WHERE f.fechaHora > :ahora ORDER BY f.fechaHora ASC")
    List<Funcion> findFuncionesDisponibles(@Param("ahora") LocalDateTime ahora);
    
    // Buscar funciones futuras de una película específica
    @Query("SELECT f FROM Funcion f WHERE f.pelicula.id = :peliculaId AND f.fechaHora > :ahora ORDER BY f.fechaHora ASC")
    List<Funcion> findFuncionesDisponiblesByPeliculaId(@Param("peliculaId") Long peliculaId, @Param("ahora") LocalDateTime ahora);
    
    // Buscar funciones por fecha ordenadas por hora
    @Query("SELECT f FROM Funcion f WHERE DATE(f.fechaHora) = :fecha ORDER BY f.fechaHora ASC")
    List<Funcion> findByFechaOrdenado(@Param("fecha") LocalDate fecha);
}