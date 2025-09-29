package com.utp.cinerama.cinerama.service.impl;

import com.utp.cinerama.cinerama.model.Pelicula;
import com.utp.cinerama.cinerama.repository.PeliculaRepository;
import com.utp.cinerama.cinerama.service.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeliculaServiceImpl implements PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Override
    public List<Pelicula> obtenerTodasLasPeliculas() {
        return peliculaRepository.findAll();
    }

    @Override
    public Optional<Pelicula> obtenerPeliculaPorId(Long id) {
        return peliculaRepository.findById(id);
    }

    @Override
    public Pelicula crearPelicula(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    @Override
    public Pelicula actualizarPelicula(Long id, Pelicula pelicula) {
        return peliculaRepository.findById(id)
                .map(p -> {
                    p.setTitulo(pelicula.getTitulo());
                    p.setGenero(pelicula.getGenero());
                    p.setDuracion(pelicula.getDuracion());
                    p.setClasificacion(pelicula.getClasificacion());
                    p.setSinopsis(pelicula.getSinopsis());
                    return peliculaRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
    }

    @Override
    public void eliminarPelicula(Long id) {
        peliculaRepository.deleteById(id);
    }

    @Override
    public List<Pelicula> buscarPorGenero(String genero) {
        return peliculaRepository.findByGenero(genero);
    }

    @Override
    public List<Pelicula> buscarPorTitulo(String titulo) {
        return peliculaRepository.findByTituloContainingIgnoreCase(titulo);
    }

    @Override
    public List<Pelicula> buscarPorClasificacion(String clasificacion) {
        return peliculaRepository.findByClasificacion(clasificacion);
    }
}