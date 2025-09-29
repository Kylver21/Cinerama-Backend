package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.DetalleVentaProducto;
import com.utp.cinerama.cinerama.service.DetalleVentaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-venta-producto")
public class DetalleVentaProductoController {

    @Autowired
    private DetalleVentaProductoService detalleVentaProductoService;

    @GetMapping
    public List<DetalleVentaProducto> obtenerTodosLosDetalles() {
        return detalleVentaProductoService.obtenerTodosLosDetalles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaProducto> obtenerDetallePorId(@PathVariable Long id) {
        return detalleVentaProductoService.obtenerDetallePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DetalleVentaProducto crearDetalle(@RequestBody DetalleVentaProducto detalleVentaProducto) {
        return detalleVentaProductoService.crearDetalle(detalleVentaProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleVentaProducto> actualizarDetalle(@PathVariable Long id, @RequestBody DetalleVentaProducto detalleVentaProducto) {
        try {
            return ResponseEntity.ok(detalleVentaProductoService.actualizarDetalle(id, detalleVentaProducto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        detalleVentaProductoService.eliminarDetalle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/venta/{ventaProductoId}")
    public List<DetalleVentaProducto> buscarPorVentaProducto(@PathVariable Long ventaProductoId) {
        return detalleVentaProductoService.buscarPorVentaProducto(ventaProductoId);
    }
}