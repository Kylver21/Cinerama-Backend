package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.dto.ApiResponse;
import com.utp.cinerama.cinerama.dto.TMDbMovieDTO;
import com.utp.cinerama.cinerama.service.TMDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para exponer endpoints directos de TMDb al frontend
 * Actúa como proxy para consultar películas de la API externa
 */
@RestController
@RequestMapping("/api/tmdb")
@RequiredArgsConstructor
@Slf4j
public class TMDbController {

    private final TMDbService tmdbService;

    /**
     * Endpoint para obtener películas en cartelera (now_playing)
     * Frontend puede consumir directamente sin sincronizar con la BD
     */
    @GetMapping("/en-cartelera")
    public ResponseEntity<ApiResponse<List<TMDbMovieDTO>>> obtenerEnCartelera(
            @RequestParam(defaultValue = "1") Integer page) {
        
        log.info("Solicitando películas en cartelera - página {}", page);
        
        List<TMDbMovieDTO> peliculas = tmdbService.getNowPlayingMovies(page);
        
        return ResponseEntity.ok(
                ApiResponse.success(
                        String.format("Se obtuvieron %d películas en cartelera", peliculas.size()),
                        peliculas
                )
        );
    }

    /**
     * Endpoint para obtener películas próximamente (upcoming)
     * Muestra estrenos futuros sin necesidad de sincronización
     */
    @GetMapping("/proximamente")
    public ResponseEntity<ApiResponse<List<TMDbMovieDTO>>> obtenerProximamente(
            @RequestParam(defaultValue = "1") Integer page) {
        
        log.info("Solicitando películas próximamente - página {}", page);
        
        List<TMDbMovieDTO> peliculas = tmdbService.getUpcomingMovies(page);
        
        return ResponseEntity.ok(
                ApiResponse.success(
                        String.format("Se obtuvieron %d películas próximamente", peliculas.size()),
                        peliculas
                )
        );
    }

    /**
     * Endpoint para obtener películas populares
     * Muestra las más populares según TMDb
     */
    @GetMapping("/populares")
    public ResponseEntity<ApiResponse<List<TMDbMovieDTO>>> obtenerPopulares(
            @RequestParam(defaultValue = "1") Integer page) {
        
        log.info("Solicitando películas populares - página {}", page);
        
        List<TMDbMovieDTO> peliculas = tmdbService.getPopularMovies(page);
        
        return ResponseEntity.ok(
                ApiResponse.success(
                        String.format("Se obtuvieron %d películas populares", peliculas.size()),
                        peliculas
                )
        );
    }
}
