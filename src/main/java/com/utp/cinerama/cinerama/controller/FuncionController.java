package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.dto.ActualizarFuncionDTO;
import com.utp.cinerama.cinerama.dto.ApiResponse;
import com.utp.cinerama.cinerama.dto.CrearFuncionDTO;
import com.utp.cinerama.cinerama.exception.ResourceNotFoundException;
import com.utp.cinerama.cinerama.model.Funcion;
import com.utp.cinerama.cinerama.model.Pelicula;
import com.utp.cinerama.cinerama.model.Sala;
import com.utp.cinerama.cinerama.service.FuncionService;
import com.utp.cinerama.cinerama.service.PeliculaService;
import com.utp.cinerama.cinerama.service.SalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
@Slf4j
public class FuncionController {

    private final FuncionService funcionService;
    private final PeliculaService peliculaService;
    private final SalaService salaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Funcion>>> obtenerTodasLasFunciones() {
        log.info("Obteniendo todas las funciones");
        List<Funcion> funciones = funcionService.obtenerTodasLasFunciones();
        return ResponseEntity.ok(
            ApiResponse.success("Funciones obtenidas exitosamente", funciones)
        );
    }
    
    /**
     * Obtiene solo las funciones disponibles (futuras) para compra
     * Ideal para mostrar la cartelera actual al usuario
     */
    @GetMapping("/disponibles")
    public ResponseEntity<ApiResponse<List<Funcion>>> obtenerFuncionesDisponibles() {
        log.info("Obteniendo funciones disponibles (futuras)");
        List<Funcion> funciones = funcionService.obtenerFuncionesDisponibles();
        return ResponseEntity.ok(
            ApiResponse.success("Funciones disponibles obtenidas exitosamente", funciones)
        );
    }
    
    /**
     * Obtiene las funciones disponibles para una película específica
     * Útil para cuando el usuario selecciona una película y quiere ver los horarios
     */
    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<ApiResponse<List<Funcion>>> obtenerFuncionesPorPelicula(@PathVariable Long peliculaId) {
        log.info("Obteniendo funciones para película ID: {}", peliculaId);
        List<Funcion> funciones = funcionService.obtenerFuncionesPorPelicula(peliculaId);
        return ResponseEntity.ok(
            ApiResponse.success("Funciones de la película obtenidas exitosamente", funciones)
        );
    }
    
    /**
     * Obtiene las funciones DISPONIBLES (futuras) para una película específica
     * Este es el endpoint principal para el flujo de compra
     */
    @GetMapping("/pelicula/{peliculaId}/disponibles")
    public ResponseEntity<ApiResponse<List<Funcion>>> obtenerFuncionesDisponiblesPorPelicula(@PathVariable Long peliculaId) {
        log.info("Obteniendo funciones disponibles para película ID: {}", peliculaId);
        
        // Validar que existe la película
        peliculaService.obtenerPeliculaPorId(peliculaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pelicula", "id", peliculaId));
        
        List<Funcion> funciones = funcionService.obtenerFuncionesDisponiblesPorPelicula(peliculaId);
        return ResponseEntity.ok(
            ApiResponse.success("Funciones disponibles de la película obtenidas exitosamente", funciones)
        );
    }
    
    /**
     * Obtiene las funciones por sala
     * Útil para el admin ver la programación de una sala
     */
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<ApiResponse<List<Funcion>>> obtenerFuncionesPorSala(@PathVariable Long salaId) {
        log.info("Obteniendo funciones para sala ID: {}", salaId);
        List<Funcion> funciones = funcionService.obtenerFuncionesPorSala(salaId);
        return ResponseEntity.ok(
            ApiResponse.success("Funciones de la sala obtenidas exitosamente", funciones)
        );
    }
    
    /**
     * Obtiene las funciones por fecha (cartelera del día)
     * Formato de fecha: yyyy-MM-dd (ej: 2025-11-26)
     */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<ApiResponse<List<Funcion>>> obtenerFuncionesPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        log.info("Obteniendo funciones para fecha: {}", fecha);
        List<Funcion> funciones = funcionService.obtenerFuncionesPorFecha(fecha);
        return ResponseEntity.ok(
            ApiResponse.success("Funciones del día " + fecha + " obtenidas exitosamente", funciones)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Funcion>> obtenerFuncionPorId(@PathVariable Long id) {
        log.info("Buscando función por ID: {}", id);
        Funcion funcion = funcionService.obtenerFuncionPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcion", "id", id));
        
        return ResponseEntity.ok(
            ApiResponse.success("Función obtenida exitosamente", funcion)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Funcion>> crearFuncion(@Valid @RequestBody CrearFuncionDTO dto) {
        log.info("Creando nueva función para película {} en sala {}", dto.getPeliculaId(), dto.getSalaId());
        
        // Validar que existan la película y la sala
        Pelicula pelicula = peliculaService.obtenerPeliculaPorId(dto.getPeliculaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pelicula", "id", dto.getPeliculaId()));
        
        Sala sala = salaService.obtenerSalaPorId(dto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala", "id", dto.getSalaId()));
        
        // Crear la función
        Funcion funcion = new Funcion();
        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setFechaHora(dto.getFechaHora());
        funcion.setAsientosTotales(dto.getAsientosTotales());
        funcion.setAsientosDisponibles(dto.getAsientosTotales());
        funcion.setPrecioEntrada(dto.getPrecioEntrada());
        
        Funcion nuevaFuncion = funcionService.crearFuncion(funcion);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Función creada exitosamente", nuevaFuncion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Funcion>> actualizarFuncion(
            @PathVariable Long id, 
            @Valid @RequestBody ActualizarFuncionDTO dto) {
        
        log.info("Actualizando función ID: {}", id);
        
        // Obtener la función existente
        Funcion funcionExistente = funcionService.obtenerFuncionPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcion", "id", id));
        
        // Actualizar solo los campos que vienen en el DTO
        if (dto.getFechaHora() != null) {
            funcionExistente.setFechaHora(dto.getFechaHora());
        }
        if (dto.getAsientosDisponibles() != null) {
            funcionExistente.setAsientosDisponibles(dto.getAsientosDisponibles());
        }
        if (dto.getAsientosTotales() != null) {
            funcionExistente.setAsientosTotales(dto.getAsientosTotales());
        }
        if (dto.getPrecioEntrada() != null) {
            funcionExistente.setPrecioEntrada(dto.getPrecioEntrada());
        }
        
        Funcion funcionActualizada = funcionService.actualizarFuncion(id, funcionExistente);
        
        return ResponseEntity.ok(
            ApiResponse.success("Función actualizada exitosamente", funcionActualizada)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> eliminarFuncion(@PathVariable Long id) {
        log.info("Eliminando función ID: {}", id);
        funcionService.eliminarFuncion(id);
        
        return ResponseEntity.ok(
            ApiResponse.success("Función eliminada exitosamente")
        );
    }
}