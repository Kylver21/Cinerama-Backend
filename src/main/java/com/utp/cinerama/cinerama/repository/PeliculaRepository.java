package com.utp.cinerama.cinerama.repository;

import com.utp.cinerama.cinerama.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByGenero(String genero);
    List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
    List<Pelicula> findByClasificacion(String clasificacion);
}