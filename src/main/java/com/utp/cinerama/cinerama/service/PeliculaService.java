package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.dto.SyncResponseDTO;
import com.utp.cinerama.cinerama.model.Pelicula;

import java.util.List;
import java.util.Optional;

public interface PeliculaService {
    List<Pelicula> obtenerTodasLasPeliculas();
    Optional<Pelicula> obtenerPeliculaPorId(Long id);
    Pelicula crearPelicula(Pelicula pelicula);
    Pelicula actualizarPelicula(Long id, Pelicula pelicula);
    void eliminarPelicula(Long id);
    List<Pelicula> buscarPorGenero(String genero);
    List<Pelicula> buscarPorTitulo(String titulo);
    List<Pelicula> buscarPorClasificacion(String clasificacion);
    
    // Nuevos métodos para integración con TMDb
    SyncResponseDTO sincronizarPeliculasDesdeAPI(Integer paginas);
    List<Pelicula> obtenerPeliculasActivas();
    List<Pelicula> obtenerPeliculasPorPopularidad();
    List<Pelicula> obtenerPeliculasPorVoto();
    Optional<Pelicula> obtenerPorTmdbId(Long tmdbId);
}