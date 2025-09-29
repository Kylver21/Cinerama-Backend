package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Pelicula;
import com.utp.cinerama.cinerama.service.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @GetMapping
    public List<Pelicula> obtenerTodasLasPeliculas() {
        return peliculaService.obtenerTodasLasPeliculas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> obtenerPeliculaPorId(@PathVariable Long id) {
        return peliculaService.obtenerPeliculaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pelicula crearPelicula(@RequestBody Pelicula pelicula) {
        return peliculaService.crearPelicula(pelicula);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> actualizarPelicula(@PathVariable Long id, @RequestBody Pelicula pelicula) {
        try {
            return ResponseEntity.ok(peliculaService.actualizarPelicula(id, pelicula));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPelicula(@PathVariable Long id) {
        peliculaService.eliminarPelicula(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/genero/{genero}")
    public List<Pelicula> buscarPorGenero(@PathVariable String genero) {
        return peliculaService.buscarPorGenero(genero);
    }

    @GetMapping("/titulo/{titulo}")
    public List<Pelicula> buscarPorTitulo(@PathVariable String titulo) {
        return peliculaService.buscarPorTitulo(titulo);
    }

    @GetMapping("/clasificacion/{clasificacion}")
    public List<Pelicula> buscarPorClasificacion(@PathVariable String clasificacion) {
        return peliculaService.buscarPorClasificacion(clasificacion);
    }
}