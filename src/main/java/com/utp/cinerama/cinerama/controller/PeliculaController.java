package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.dto.ApiResponse;
import com.utp.cinerama.cinerama.dto.PagedResponse;
import com.utp.cinerama.cinerama.dto.SyncResponseDTO;
import com.utp.cinerama.cinerama.exception.ResourceNotFoundException;
import com.utp.cinerama.cinerama.model.Pelicula;
import com.utp.cinerama.cinerama.service.PeliculaService;
import com.utp.cinerama.cinerama.util.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    // ========== ENDPOINTS PAGINADOS (NUEVOS - PARA ANGULAR) ==========
    
    /**
     * Obtiene películas paginadas con ordenamiento
     * 
     * @param page Número de página (0-indexed, default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenamiento (default: id)
     * @return Respuesta paginada con películas
     * 
     * Ejemplo: GET /api/peliculas/paginadas?page=0&size=10&sortBy=popularidad
     */
    @GetMapping("/paginadas")
    public ResponseEntity<ApiResponse<PagedResponse<Pelicula>>> obtenerPeliculasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        log.info("Obteniendo películas paginadas - Page: {}, Size: {}, Sort: {}", page, size, sortBy);
        
        Page<Pelicula> peliculasPage = peliculaService.obtenerPeliculasPaginadas(page, size, sortBy);
        PagedResponse<Pelicula> pagedResponse = PaginationUtils.toPagedResponse(peliculasPage);
        
        return ResponseEntity.ok(
            ApiResponse.success("Películas obtenidas exitosamente", pagedResponse)
        );
    }

    /**
     * Busca películas por género con paginación
     * 
     * @param genero Género a buscar (ej: "Action", "Drama")
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return Respuesta paginada con películas del género especificado
     * 
     * Ejemplo: GET /api/peliculas/genero/paginado?genero=Action&page=0&size=10
     */
    @GetMapping("/genero/paginado")
    public ResponseEntity<ApiResponse<PagedResponse<Pelicula>>> buscarPorGeneroPaginado(
            @RequestParam String genero,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Buscando películas por género paginado - Género: {}, Page: {}, Size: {}", genero, page, size);
        
        Page<Pelicula> peliculasPage = peliculaService.buscarPorGeneroPaginado(genero, page, size);
        PagedResponse<Pelicula> pagedResponse = PaginationUtils.toPagedResponse(peliculasPage);
        
        return ResponseEntity.ok(
            ApiResponse.success("Películas del género '" + genero + "' obtenidas exitosamente", pagedResponse)
        );
    }

    /**
     * Busca películas por título con paginación
     * 
     * @param titulo Título o parte del título a buscar
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return Respuesta paginada con películas que coinciden
     * 
     * Ejemplo: GET /api/peliculas/titulo/paginado?titulo=Spider&page=0&size=10
     */
    @GetMapping("/titulo/paginado")
    public ResponseEntity<ApiResponse<PagedResponse<Pelicula>>> buscarPorTituloPaginado(
            @RequestParam String titulo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Buscando películas por título paginado - Título: {}, Page: {}, Size: {}", titulo, page, size);
        
        Page<Pelicula> peliculasPage = peliculaService.buscarPorTituloPaginado(titulo, page, size);
        PagedResponse<Pelicula> pagedResponse = PaginationUtils.toPagedResponse(peliculasPage);
        
        return ResponseEntity.ok(
            ApiResponse.success("Películas con título '" + titulo + "' obtenidas exitosamente", pagedResponse)
        );
    }

    // ========== ENDPOINTS NO PAGINADOS (LEGACY - MANTENER COMPATIBILIDAD) ==========
    
    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<List<Pelicula>>> obtenerPeliculasActivas() {
        log.info("Obteniendo películas activas");
        List<Pelicula> peliculas = peliculaService.obtenerPeliculasActivas();
        return ResponseEntity.ok(
            ApiResponse.success("Películas activas obtenidas exitosamente", peliculas)
        );
    }

    @GetMapping("/populares")
    public ResponseEntity<ApiResponse<List<Pelicula>>> obtenerPorPopularidad() {
        log.info("Obteniendo películas por popularidad");
        List<Pelicula> peliculas = peliculaService.obtenerPeliculasPorPopularidad();
        return ResponseEntity.ok(
            ApiResponse.success("Películas populares obtenidas exitosamente", peliculas)
        );
    }

    @GetMapping("/mejor-valoradas")
    public ResponseEntity<ApiResponse<List<Pelicula>>> obtenerPorVoto() {
        log.info("Obteniendo películas mejor valoradas");
        List<Pelicula> peliculas = peliculaService.obtenerPeliculasPorVoto();
        return ResponseEntity.ok(
            ApiResponse.success("Películas mejor valoradas obtenidas exitosamente", peliculas)
        );
    }

    @GetMapping("/genero/{genero}")
    public ResponseEntity<ApiResponse<List<Pelicula>>> buscarPorGenero(@PathVariable String genero) {
        log.info("Buscando películas por género: {}", genero);
        List<Pelicula> peliculas = peliculaService.buscarPorGenero(genero);
        return ResponseEntity.ok(
            ApiResponse.success("Películas del género '" + genero + "' obtenidas exitosamente", peliculas)
        );
    }

    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<ApiResponse<List<Pelicula>>> buscarPorTitulo(@PathVariable String titulo) {
        log.info("Buscando películas por título: {}", titulo);
        List<Pelicula> peliculas = peliculaService.buscarPorTitulo(titulo);
        return ResponseEntity.ok(
            ApiResponse.success("Películas con título '" + titulo + "' obtenidas exitosamente", peliculas)
        );
    }

    @GetMapping("/clasificacion/{clasificacion}")
    public ResponseEntity<ApiResponse<List<Pelicula>>> buscarPorClasificacion(@PathVariable String clasificacion) {
        log.info("Buscando películas por clasificación: {}", clasificacion);
        List<Pelicula> peliculas = peliculaService.buscarPorClasificacion(clasificacion);
        return ResponseEntity.ok(
            ApiResponse.success("Películas con clasificación '" + clasificacion + "' obtenidas exitosamente", peliculas)
        );
    }

    @GetMapping("/tmdb/{tmdbId}")
    public ResponseEntity<ApiResponse<Pelicula>> obtenerPorTmdbId(@PathVariable Long tmdbId) {
        log.info("Buscando película por TMDb ID: {}", tmdbId);
        Pelicula pelicula = peliculaService.obtenerPorTmdbId(tmdbId)
                .orElseThrow(() -> new ResourceNotFoundException("Pelicula", "tmdbId", tmdbId));
        
        return ResponseEntity.ok(
            ApiResponse.success("Película obtenida exitosamente", pelicula)
        );
    }

    // ========== CRUD BÁSICO CON VALIDACIONES ==========

    /**
     * Obtiene todas las películas sin paginación
     * NOTA: Para grandes volúmenes, usar /paginadas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Pelicula>>> obtenerTodasLasPeliculas() {
        log.info("Obteniendo todas las películas");
        List<Pelicula> peliculas = peliculaService.obtenerTodasLasPeliculas();
        return ResponseEntity.ok(
            ApiResponse.success("Películas obtenidas exitosamente", peliculas)
        );
    }

    /**
     * Obtiene una película específica por su ID
     * 
     * @param id ID de la película
     * @return Película encontrada
     * @throws ResourceNotFoundException si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pelicula>> obtenerPeliculaPorId(@PathVariable Long id) {
        log.info("Buscando película por ID: {}", id);
        Pelicula pelicula = peliculaService.obtenerPeliculaPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pelicula", "id", id));
        
        return ResponseEntity.ok(
            ApiResponse.success("Película obtenida exitosamente", pelicula)
        );
    }

    /**
     * Crea una nueva película
     * NOTA: Validación automática con @Valid
     * 
     * @param pelicula Datos de la película a crear
     * @return Película creada con ID asignado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Pelicula>> crearPelicula(@Valid @RequestBody Pelicula pelicula) {
        log.info("Creando nueva película: {}", pelicula.getTitulo());
        Pelicula nuevaPelicula = peliculaService.crearPelicula(pelicula);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Película creada exitosamente", nuevaPelicula));
    }

    /**
     * Actualiza una película existente
     * 
     * @param id ID de la película a actualizar
     * @param pelicula Datos actualizados
     * @return Película actualizada
     * @throws ResourceNotFoundException si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Pelicula>> actualizarPelicula(
            @PathVariable Long id, 
            @Valid @RequestBody Pelicula pelicula) {
        
        log.info("Actualizando película ID: {}", id);
        Pelicula peliculaActualizada = peliculaService.actualizarPelicula(id, pelicula);
        
        return ResponseEntity.ok(
            ApiResponse.success("Película actualizada exitosamente", peliculaActualizada)
        );
    }

    /**
     * Elimina una película (soft delete)
     * 
     * @param id ID de la película a eliminar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> eliminarPelicula(@PathVariable Long id) {
        log.info("Eliminando película ID: {}", id);
        peliculaService.eliminarPelicula(id);
        
        return ResponseEntity.ok(
            ApiResponse.success("Película eliminada exitosamente")
        );
    }

    // ========== SINCRONIZACIÓN CON TMDb API ==========

    /**
     * NUEVO: Agrega una película específica desde TMDb a la cartelera local
     * El admin selecciona una película del catálogo TMDb y la añade al sistema
     * 
     * @param tmdbId ID de la película en TMDb
     * @return Película guardada en BD local
     * 
     * Ejemplo: POST /api/peliculas/agregar-desde-tmdb?tmdbId=634649
     */
    @PostMapping("/agregar-desde-tmdb")
    public ResponseEntity<ApiResponse<Pelicula>> agregarPeliculaDesdeTMDb(
            @RequestParam Long tmdbId) {
        
        log.info("Admin agregando película desde TMDb - ID: {}", tmdbId);
        Pelicula pelicula = peliculaService.agregarPeliculaDesdeTMDb(tmdbId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Película agregada exitosamente. Ahora puedes crear funciones para ella.", 
                    pelicula
                ));
    }

    /**
     * DEPRECADO: Sincroniza películas desde TMDb API (Now Playing)
     * 
     * ⚠️ NOTA: Este endpoint guarda TODAS las películas de TMDb en tu BD.
     * Se recomienda usar /agregar-desde-tmdb para agregar películas específicas.
     * Los endpoints /api/tmdb/* proveen catálogo completo sin guardar.
     * 
     * @param paginas Número de páginas a sincronizar (1-5). Default: 1
     * @return Resultado de la sincronización con estadísticas
     * 
     * @deprecated Usar {@link #agregarPeliculaDesdeTMDb(Long)} para agregar películas individuales
     */
    @Deprecated
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<SyncResponseDTO>> sincronizarPeliculas(
            @RequestParam(defaultValue = "1") Integer paginas) {
        
        log.info("Iniciando sincronización de películas (páginas: {})", paginas);
        SyncResponseDTO resultado = peliculaService.sincronizarPeliculasDesdeAPI(paginas);
        
        return ResponseEntity.ok(
            ApiResponse.success("Sincronización completada exitosamente", resultado)
        );
    }

    /**
     * Endpoint de prueba de conexión con TMDb API
     * 
     * @return Mensaje de estado de la conexión
     */
    @GetMapping("/test-connection")
    public ResponseEntity<ApiResponse<String>> testTMDbConnection() {
        peliculaService.sincronizarPeliculasDesdeAPI(1);
        return ResponseEntity.ok(
            ApiResponse.success("Conexión exitosa con TMDb API", "OK")
        );
    }
}