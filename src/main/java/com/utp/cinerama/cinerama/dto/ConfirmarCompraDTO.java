package com.utp.cinerama.cinerama.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * DTO para confirmar una compra de boletos y productos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmarCompraDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "La lista de asientos es obligatoria")
    @Size(min = 1, message = "Debe seleccionar al menos un asiento")
    private List<Long> asientoIds;

    @NotNull(message = "El ID de la función es obligatorio")
    private Long funcionId;

    // Productos opcionales
    private List<DetalleProductoDTO> productos;

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago; // TARJETA, EFECTIVO, YAPE, PLIN

    /**
     * DTO interno para detalles de productos
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleProductoDTO {
        
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 50, message = "La cantidad máxima es 50")
        private Integer cantidad;
    }
}
