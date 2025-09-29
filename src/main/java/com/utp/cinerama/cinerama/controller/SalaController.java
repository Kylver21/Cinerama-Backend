package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Sala;
import com.utp.cinerama.cinerama.service.SalaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    @Autowired
    private SalaService salaService;

    @GetMapping
    public List<Sala> obtenerTodasLasSalas() {
        return salaService.obtenerTodasLasSalas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sala> obtenerSalaPorId(@PathVariable Long id) {
        return salaService.obtenerSalaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Sala crearSala(@RequestBody Sala sala) {
        return salaService.crearSala(sala);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sala> actualizarSala(@PathVariable Long id, @RequestBody Sala sala) {
        try {
            return ResponseEntity.ok(salaService.actualizarSala(id, sala));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSala(@PathVariable Long id) {
        salaService.eliminarSala(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tipo/{tipo}")
    public List<Sala> buscarPorTipo(@PathVariable Sala.TipoSala tipo) {
        return salaService.buscarPorTipo(tipo);
    }

    @GetMapping("/activas")
    public List<Sala> buscarSalasActivas() {
        return salaService.buscarSalasActivas();
    }
}