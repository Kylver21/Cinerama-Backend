package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    
    // Buscar por TMDb ID (unico)
    Optional<Pelicula> findByTmdbId(Long tmdbId);
    
    // Verificar si existe por TMDb ID
    boolean existsByTmdbId(Long tmdbId);
    
    // Busquedas SIN paginacion (para compatibilidad)
    List<Pelicula> findByGeneroContainingIgnoreCase(String genero);
    List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
    List<Pelicula> findByClasificacion(String clasificacion);
    
    // Busquedas CON paginacion
    Page<Pelicula> findByGeneroContainingIgnoreCase(String genero, Pageable pageable);
    Page<Pelicula> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    Page<Pelicula> findByClasificacion(String clasificacion, Pageable pageable);
    
    // Buscar peliculas activas
    List<Pelicula> findByActivaTrue();
    Page<Pelicula> findByActivaTrue(Pageable pageable);
    
    // Buscar por popularidad
    List<Pelicula> findByOrderByPopularidadDesc();
    Page<Pelicula> findByOrderByPopularidadDesc(Pageable pageable);
    
    // Buscar por voto promedio
    List<Pelicula> findByOrderByVotoPromedioDesc();
    Page<Pelicula> findByOrderByVotoPromedioDesc(Pageable pageable);
}