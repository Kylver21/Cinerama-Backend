package com.utp.cinerama.cinerama.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * DTO para calcular el total de una compra antes de confirmar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalcularTotalDTO {

    @NotNull(message = "El ID de la función es obligatorio")
    private Long funcionId;

    @NotNull(message = "La lista de asientos es obligatoria")
    @Size(min = 1, message = "Debe seleccionar al menos un asiento")
    private List<Long> asientoIds;

    // Productos opcionales
    private List<DetalleProductoDTO> productos;

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
