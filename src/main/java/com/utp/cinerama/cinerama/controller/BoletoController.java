package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Boleto;
import com.utp.cinerama.cinerama.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boletos")
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @GetMapping
    public List<Boleto> obtenerTodosLosBoletos() {
        return boletoService.obtenerTodosLosBoletos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boleto> obtenerBoletoPorId(@PathVariable Long id) {
        return boletoService.obtenerBoletoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Boleto crearBoleto(@RequestBody Boleto boleto) {
        return boletoService.crearBoleto(boleto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boleto> actualizarBoleto(@PathVariable Long id, @RequestBody Boleto boleto) {
        try {
            return ResponseEntity.ok(boletoService.actualizarBoleto(id, boleto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBoleto(@PathVariable Long id) {
        boletoService.eliminarBoleto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Boleto> buscarPorCliente(@PathVariable Long clienteId) {
        return boletoService.buscarPorCliente(clienteId);
    }

    @GetMapping("/funcion/{funcionId}")
    public List<Boleto> buscarPorFuncion(@PathVariable Long funcionId) {
        return boletoService.buscarPorFuncion(funcionId);
    }

    @GetMapping("/estado/{estado}")
    public List<Boleto> buscarPorEstado(@PathVariable Boleto.EstadoBoleto estado) {
        return boletoService.buscarPorEstado(estado);
    }
}