package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Funcion;
import com.utp.cinerama.cinerama.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    @GetMapping
    public List<Funcion> obtenerTodasLasFunciones() {
        return funcionService.obtenerTodasLasFunciones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcion> obtenerFuncionPorId(@PathVariable Long id) {
        return funcionService.obtenerFuncionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Funcion crearFuncion(@RequestBody Funcion funcion) {
        return funcionService.crearFuncion(funcion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcion> actualizarFuncion(@PathVariable Long id, @RequestBody Funcion funcion) {
        try {
            return ResponseEntity.ok(funcionService.actualizarFuncion(id, funcion));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFuncion(@PathVariable Long id) {
        funcionService.eliminarFuncion(id);
        return ResponseEntity.noContent().build();
    }
}