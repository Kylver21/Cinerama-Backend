package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.dto.ApiResponse;
import com.utp.cinerama.cinerama.dto.CalcularTotalDTO;
import com.utp.cinerama.cinerama.dto.ConfirmarCompraDTO;
import com.utp.cinerama.cinerama.dto.ConfirmacionCompraDTO;
import com.utp.cinerama.cinerama.dto.TotalCompraDTO;
import com.utp.cinerama.cinerama.exception.ResourceNotFoundException;
import com.utp.cinerama.cinerama.model.*;
import com.utp.cinerama.cinerama.repository.AsientoRepository;
import com.utp.cinerama.cinerama.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para orquestar el proceso de compra completo
 */
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@Slf4j
public class CompraController {

    private final AsientoService asientoService;
    private final AsientoRepository asientoRepository;
    private final BoletoService boletoService;
    private final PagoService pagoService;
    private final ClienteService clienteService;
    private final FuncionService funcionService;
    private final ProductoService productoService;
    private final VentaProductoService ventaProductoService;
    private final DetalleVentaProductoService detalleVentaProductoService;

    /**
     * Endpoint principal para confirmar una compra completa
     * Orquesta: confirmación de asientos, creación de boletos, registro de pago
     */
    @PostMapping("/confirmar")
    @Transactional
    public ResponseEntity<ApiResponse<ConfirmacionCompraDTO>> confirmarCompra(
            @Valid @RequestBody ConfirmarCompraDTO dto) {
        
        log.info("Iniciando confirmación de compra para cliente {} - {} asientos", 
                 dto.getClienteId(), dto.getAsientoIds().size());

        try {
            // 1. Validar que existan cliente y función
            Cliente cliente = clienteService.obtenerClientePorId(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId()));
            
            log.info("Cliente encontrado: {} {}", cliente.getNombre(), cliente.getApellido());
            
            Funcion funcion = funcionService.obtenerFuncionPorId(dto.getFuncionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Funcion", "id", dto.getFuncionId()));
            
            log.info("Función encontrada: {} - {}", funcion.getId(), funcion.getPelicula().getTitulo());

            // 2. Confirmar asientos reservados y crear boletos
            List<ConfirmacionCompraDTO.BoletoResumenDTO> boletosResumen = new ArrayList<>();
            BigDecimal totalBoletos = BigDecimal.ZERO;

            for (Long asientoId : dto.getAsientoIds()) {
                log.info("Procesando asiento ID: {}", asientoId);
                
                // Confirmar el asiento (cambia de RESERVADO a OCUPADO)
                Asiento asiento = asientoService.confirmarReserva(asientoId);
                log.info("Asiento {} confirmado - Estado: {}", asiento.getCodigoAsiento(), asiento.getEstado());
                
                // Crear boleto asociado al asiento
                Boleto boleto = new Boleto();
                boleto.setCliente(cliente);
                boleto.setFuncion(funcion);
                boleto.setAsiento(asiento);
                boleto.setPrecio(funcion.getPrecioEntrada().doubleValue());
                boleto.setEstado(Boleto.EstadoBoleto.PAGADO);
                boleto.setFechaCompra(LocalDateTime.now());
                
                log.info("Creando boleto para asiento {}", asiento.getCodigoAsiento());
                Boleto boletoCreado = boletoService.crearBoleto(boleto);
                log.info("Boleto creado con ID: {}", boletoCreado.getId());
                
                // Agregar al resumen
                boletosResumen.add(ConfirmacionCompraDTO.BoletoResumenDTO.builder()
                        .boletoId(boletoCreado.getId())
                        .pelicula(funcion.getPelicula().getTitulo())
                        .sala(funcion.getSala().getNombre())
                        .fechaHora(funcion.getFechaHora())
                        .asiento(asiento.getCodigoAsiento())
                        .precio(funcion.getPrecioEntrada())
                        .build());
                
                totalBoletos = totalBoletos.add(funcion.getPrecioEntrada());
            }

            // 3. Procesar productos opcionales (si los hay)
            List<ConfirmacionCompraDTO.ProductoResumenDTO> productosResumen = new ArrayList<>();
            BigDecimal totalProductos = BigDecimal.ZERO;

            if (dto.getProductos() != null && !dto.getProductos().isEmpty()) {
                // Crear venta de productos
                VentaProducto ventaProducto = new VentaProducto();
                ventaProducto.setCliente(cliente);
                ventaProducto.setMetodoPago(dto.getMetodoPago());
                ventaProducto.setCompletada(true);
                
                VentaProducto ventaCreada = ventaProductoService.crearVenta(ventaProducto);

                for (ConfirmarCompraDTO.DetalleProductoDTO detalle : dto.getProductos()) {
                    Producto producto = productoService.obtenerProductoPorId(detalle.getProductoId())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalle.getProductoId()));
                    
                    // Crear detalle de venta
                    DetalleVentaProducto detalleVenta = new DetalleVentaProducto();
                    detalleVenta.setVentaProducto(ventaCreada);
                    detalleVenta.setProducto(producto);
                    detalleVenta.setCantidad(detalle.getCantidad());
                    
                    detalleVentaProductoService.crearDetalle(detalleVenta);
                    
                    // Calcular subtotal para el resumen
                    BigDecimal precioUnitario = BigDecimal.valueOf(producto.getPrecio());
                    BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    
                    // Agregar al resumen
                    productosResumen.add(ConfirmacionCompraDTO.ProductoResumenDTO.builder()
                            .nombreProducto(producto.getNombre())
                            .cantidad(detalle.getCantidad())
                            .precioUnitario(precioUnitario)
                            .subtotal(subtotal)
                            .build());
                    
                    totalProductos = totalProductos.add(subtotal);
                }
            }

            // 4. Calcular total y crear pago
            BigDecimal montoTotal = totalBoletos.add(totalProductos);
            
            Pago pago = new Pago();
            pago.setCliente(cliente);
            pago.setMonto(montoTotal.doubleValue());
            pago.setMetodoPago(dto.getMetodoPago());
            pago.setTipoComprobante("BOLETA"); // Por defecto
            pago.setEstado(Pago.EstadoPago.COMPLETADO);
            
            Pago pagoCreado = pagoService.crearPago(pago);

            // 5. Generar número de confirmación único
            String numeroConfirmacion = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 6. Construir respuesta completa
            ConfirmacionCompraDTO confirmacion = ConfirmacionCompraDTO.builder()
                    .numeroConfirmacion(numeroConfirmacion)
                    .fechaCompra(LocalDateTime.now())
                    .totalPagado(montoTotal)
                    .clienteId(cliente.getId())
                    .nombreCliente(cliente.getNombre())
                    .boletos(boletosResumen)
                    .productos(productosResumen)
                    .pago(ConfirmacionCompraDTO.PagoResumenDTO.builder()
                            .pagoId(pagoCreado.getId())
                            .metodoPago(pagoCreado.getMetodoPago())
                            .estado(pagoCreado.getEstado().name())
                            .monto(montoTotal)
                            .fechaPago(LocalDateTime.now())
                            .build())
                    .build();

            log.info("Compra confirmada exitosamente - Confirmación: {} - Total: {}", 
                     numeroConfirmacion, montoTotal);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Compra confirmada exitosamente", confirmacion));

        } catch (Exception e) {
            log.error("Error al confirmar compra: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Endpoint para calcular el total de una compra antes de confirmarla
     * Permite al usuario ver el desglose de precios
     */
    @PostMapping("/calcular-total")
    public ResponseEntity<ApiResponse<TotalCompraDTO>> calcularTotal(
            @Valid @RequestBody CalcularTotalDTO dto) {
        
        log.info("Calculando total para función {} con {} asientos", 
                 dto.getFuncionId(), dto.getAsientoIds().size());

        // 1. Validar función y obtener precio
        Funcion funcion = funcionService.obtenerFuncionPorId(dto.getFuncionId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcion", "id", dto.getFuncionId()));

        // 2. Calcular total de boletos
        List<TotalCompraDTO.DetalleAsiento> detalleAsientos = new ArrayList<>();
        BigDecimal totalBoletos = BigDecimal.ZERO;

        for (Long asientoId : dto.getAsientoIds()) {
            Asiento asiento = asientoRepository.findById(asientoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Asiento", "id", asientoId));
            
            detalleAsientos.add(TotalCompraDTO.DetalleAsiento.builder()
                    .asientoId(asiento.getId())
                    .codigoAsiento(asiento.getCodigoAsiento())
                    .precio(funcion.getPrecioEntrada())
                    .build());
            
            totalBoletos = totalBoletos.add(funcion.getPrecioEntrada());
        }

        // 3. Calcular total de productos (si los hay)
        List<TotalCompraDTO.DetalleProducto> detalleProductos = new ArrayList<>();
        BigDecimal totalProductos = BigDecimal.ZERO;

        if (dto.getProductos() != null && !dto.getProductos().isEmpty()) {
            for (CalcularTotalDTO.DetalleProductoDTO detalle : dto.getProductos()) {
                Producto producto = productoService.obtenerProductoPorId(detalle.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalle.getProductoId()));
                
                BigDecimal precioUnitario = BigDecimal.valueOf(producto.getPrecio());
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                
                detalleProductos.add(TotalCompraDTO.DetalleProducto.builder()
                        .productoId(producto.getId())
                        .nombreProducto(producto.getNombre())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(precioUnitario)
                        .subtotal(subtotal)
                        .build());
                
                totalProductos = totalProductos.add(subtotal);
            }
        }

        // 4. Calcular total general
        BigDecimal totalGeneral = totalBoletos.add(totalProductos);

        // 5. Construir respuesta
        TotalCompraDTO response = TotalCompraDTO.builder()
                .totalBoletos(totalBoletos)
                .totalProductos(totalProductos)
                .totalGeneral(totalGeneral)
                .cantidadBoletos(dto.getAsientoIds().size())
                .detalleAsientos(detalleAsientos)
                .detalleProductos(detalleProductos)
                .build();

        log.info("Total calculado: {} (boletos: {}, productos: {})", 
                 totalGeneral, totalBoletos, totalProductos);

        return ResponseEntity.ok(
                ApiResponse.success("Total calculado exitosamente", response)
        );
    }
}
