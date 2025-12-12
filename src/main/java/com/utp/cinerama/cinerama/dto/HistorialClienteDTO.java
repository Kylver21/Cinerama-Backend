package com.utp.cinerama.cinerama.dto;

import com.utp.cinerama.cinerama.model.Boleto;
import com.utp.cinerama.cinerama.model.VentaProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialClienteDTO {
    private List<Boleto> boletos; // solo PAGADO/USADO
    private List<VentaProducto> comprasProductos; // solo completadas
}
