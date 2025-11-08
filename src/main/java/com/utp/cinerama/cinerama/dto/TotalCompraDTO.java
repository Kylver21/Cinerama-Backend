package com.utp.cinerama.cinerama.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de respuesta con el desglose del total de compra
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalCompraDTO {

    private BigDecimal totalBoletos;
    private BigDecimal totalProductos;
    private BigDecimal totalGeneral;
    private Integer cantidadBoletos;
    private List<DetalleAsiento> detalleAsientos;
    private List<DetalleProducto> detalleProductos;

    /**
     * Detalle de cada asiento
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleAsiento {
        private Long asientoId;
        private String codigoAsiento;
        private BigDecimal precio;
    }

    /**
     * Detalle de cada producto
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleProducto {
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
