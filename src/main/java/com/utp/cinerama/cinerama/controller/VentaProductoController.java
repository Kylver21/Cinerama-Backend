package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Cliente;
import com.utp.cinerama.cinerama.model.VentaProducto;
import com.utp.cinerama.cinerama.service.VentaProductoService;
import com.utp.cinerama.cinerama.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas-productos")
public class VentaProductoController {

    @Autowired
    private VentaProductoService ventaProductoService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<VentaProducto> obtenerTodasLasVentas() {
        return ventaProductoService.obtenerTodasLasVentas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaProducto> obtenerVentaPorId(@PathVariable Long id) {
        return ventaProductoService.obtenerVentaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VentaProducto> crearVenta(@RequestParam Long clienteId, @RequestParam String metodoPago) {
        Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        VentaProducto nuevaVenta = ventaProductoService.crearVenta(new VentaProducto(clienteOpt.get(), metodoPago, false));
        return ResponseEntity.ok(nuevaVenta);
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<VentaProducto> completarVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventaProductoService.completarVenta(id));
    }
}