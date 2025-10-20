package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.dto.ApiResponse;
import com.utp.cinerama.cinerama.exception.ResourceNotFoundException;
import com.utp.cinerama.cinerama.model.Funcion;
import com.utp.cinerama.cinerama.service.FuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
@Slf4j
public class FuncionController {

    private final FuncionService funcionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Funcion>>> obtenerTodasLasFunciones() {
        log.info("Obteniendo todas las funciones");
        List<Funcion> funciones = funcionService.obtenerTodasLasFunciones();
        return ResponseEntity.ok(
            ApiResponse.success("Funciones obtenidas exitosamente", funciones)
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
    public ResponseEntity<ApiResponse<Funcion>> crearFuncion(@Valid @RequestBody Funcion funcion) {
        log.info("Creando nueva función");
        Funcion nuevaFuncion = funcionService.crearFuncion(funcion);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Función creada exitosamente", nuevaFuncion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Funcion>> actualizarFuncion(
            @PathVariable Long id, 
            @Valid @RequestBody Funcion funcion) {
        
        log.info("Actualizando función ID: {}", id);
        Funcion funcionActualizada = funcionService.actualizarFuncion(id, funcion);
        
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