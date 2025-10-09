package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.dto.SyncResponseDTO;
import com.utp.cinerama.cinerama.model.Pelicula;
import com.utp.cinerama.cinerama.service.PeliculaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
@Slf4j
public class PeliculaController {

    private final PeliculaService peliculaService;

    // ========== Búsquedas Específicas (DEBEN IR PRIMERO) ==========
    
    @GetMapping("/activas")
    public List<Pelicula> obtenerPeliculasActivas() {
        log.info("📋 Obteniendo películas activas");
        return peliculaService.obtenerPeliculasActivas();
    }

    @GetMapping("/populares")
    public List<Pelicula> obtenerPorPopularidad() {
        log.info("⭐ Obteniendo películas por popularidad");
        return peliculaService.obtenerPeliculasPorPopularidad();
    }

    @GetMapping("/mejor-valoradas")
    public List<Pelicula> obtenerPorVoto() {
        log.info("🏆 Obteniendo películas mejor valoradas");
        return peliculaService.obtenerPeliculasPorVoto();
    }

    @GetMapping("/genero/{genero}")
    public List<Pelicula> buscarPorGenero(@PathVariable String genero) {
        log.info("🎭 Buscando películas por género: {}", genero);
        return peliculaService.buscarPorGenero(genero);
    }

    @GetMapping("/titulo/{titulo}")
    public List<Pelicula> buscarPorTitulo(@PathVariable String titulo) {
        log.info("🔍 Buscando películas por título: {}", titulo);
        return peliculaService.buscarPorTitulo(titulo);
    }

    @GetMapping("/clasificacion/{clasificacion}")
    public List<Pelicula> buscarPorClasificacion(@PathVariable String clasificacion) {
        log.info("🎬 Buscando películas por clasificación: {}", clasificacion);
        return peliculaService.buscarPorClasificacion(clasificacion);
    }

    @GetMapping("/tmdb/{tmdbId}")
    public ResponseEntity<Pelicula> obtenerPorTmdbId(@PathVariable Long tmdbId) {
        log.info("🆔 Buscando película por TMDb ID: {}", tmdbId);
        return peliculaService.obtenerPorTmdbId(tmdbId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== CRUD Básico (van después de los específicos) ==========

    @GetMapping
    public List<Pelicula> obtenerTodasLasPeliculas() {
        log.info("📚 Obteniendo todas las películas");
        return peliculaService.obtenerTodasLasPeliculas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> obtenerPeliculaPorId(@PathVariable Long id) {
        log.info("🔎 Buscando película por ID: {}", id);
        return peliculaService.obtenerPeliculaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pelicula crearPelicula(@RequestBody Pelicula pelicula) {
        log.info("➕ Creando nueva película: {}", pelicula.getTitulo());
        return peliculaService.crearPelicula(pelicula);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> actualizarPelicula(@PathVariable Long id, @RequestBody Pelicula pelicula) {
        try {
            log.info("✏️ Actualizando película ID: {}", id);
            return ResponseEntity.ok(peliculaService.actualizarPelicula(id, pelicula));
        } catch (RuntimeException e) {
            log.error("❌ Error al actualizar película ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPelicula(@PathVariable Long id) {
        log.info("🗑️ Eliminando película ID: {}", id);
        peliculaService.eliminarPelicula(id);
        return ResponseEntity.noContent().build();
    }

    // ========== Sincronización con TMDb API ==========

    /**
     * Sincroniza películas desde TMDb API (Now Playing)
     * 
     * @param paginas Número de páginas a sincronizar (1-5). Default: 1
     * @return Resultado de la sincronización con estadísticas
     * 
     * Ejemplo: POST /api/peliculas/sync?paginas=2
     */
    @PostMapping("/sync")
    public ResponseEntity<SyncResponseDTO> sincronizarPeliculas(
            @RequestParam(defaultValue = "1") Integer paginas) {
        try {
            log.info("🔄 Iniciando sincronización de películas (páginas: {})", paginas);
            SyncResponseDTO resultado = peliculaService.sincronizarPeliculasDesdeAPI(paginas);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("❌ Error en sincronización: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SyncResponseDTO.builder()
                            .mensaje("Error al sincronizar películas: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Endpoint de prueba de conexión con TMDb
     */
    @GetMapping("/test-connection")
    public ResponseEntity<String> testTMDbConnection() {
        try {
            peliculaService.sincronizarPeliculasDesdeAPI(1);
            return ResponseEntity.ok("✅ Conexión exitosa con TMDb API");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("❌ Error de conexión: " + e.getMessage());
        }
    }
}