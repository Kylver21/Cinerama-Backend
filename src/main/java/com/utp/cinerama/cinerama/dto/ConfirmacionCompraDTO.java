package com.utp.cinerama.cinerama.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta al confirmar una compra
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmacionCompraDTO {

    private String numeroConfirmacion;
    private LocalDateTime fechaCompra;
    private BigDecimal totalPagado;
    private Long clienteId;
    private String nombreCliente;
    
    // Detalles de los boletos
    private List<BoletoResumenDTO> boletos;
    
    // Detalles de productos (si los hay)
    private List<ProductoResumenDTO> productos;
    
    // Información del pago
    private PagoResumenDTO pago;

    /**
     * DTO resumido de boleto
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoletoResumenDTO {
        private Long boletoId;
        private String pelicula;
        private String sala;
        private LocalDateTime fechaHora;
        private String asiento;
        private BigDecimal precio;
    }

    /**
     * DTO resumido de producto
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductoResumenDTO {
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }

    /**
     * DTO resumido de pago
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PagoResumenDTO {
        private Long pagoId;
        private String metodoPago;
        private String estado;
        private BigDecimal monto;
        private LocalDateTime fechaPago;
    }
}
