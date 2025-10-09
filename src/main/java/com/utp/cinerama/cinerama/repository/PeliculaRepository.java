package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    
    // Buscar por TMDb ID (único)
    Optional<Pelicula> findByTmdbId(Long tmdbId);
    
    // Verificar si existe por TMDb ID
    boolean existsByTmdbId(Long tmdbId);
    
    // Búsquedas originales
    List<Pelicula> findByGeneroContainingIgnoreCase(String genero);
    List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
    List<Pelicula> findByClasificacion(String clasificacion);
    
    // Buscar películas activas
    List<Pelicula> findByActivaTrue();
    
    // Buscar por popularidad
    List<Pelicula> findByOrderByPopularidadDesc();
    
    // Buscar por voto promedio
    List<Pelicula> findByOrderByVotoPromedioDesc();
}