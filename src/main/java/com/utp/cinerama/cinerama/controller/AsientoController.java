package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Asiento;
import com.utp.cinerama.cinerama.model.Asiento.EstadoAsiento;
import com.utp.cinerama.cinerama.model.Asiento.TipoAsiento;
import com.utp.cinerama.cinerama.service.AsientoService;
import com.utp.cinerama.cinerama.service.AsientoService.EstatisticasAsientos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AsientoController {

    private final AsientoService asientoService;

    /**
     * 🗺️ GET /api/asientos/funcion/{funcionId}
     * Obtiene el mapa completo de asientos de una función
     */
    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<Asiento>> obtenerMapaAsientos(@PathVariable Long funcionId) {
        List<Asiento> asientos = asientoService.obtenerMapaAsientos(funcionId);
        return ResponseEntity.ok(asientos);
    }

    /**
     * 🔒 POST /api/asientos/reservar/{asientoId}
     * Reserva temporalmente un asiento (5 minutos)
     */
    @PostMapping("/reservar/{asientoId}")
    public ResponseEntity<?> reservarAsiento(@PathVariable Long asientoId) {
        try {
            Asiento asiento = asientoService.reservarAsiento(asientoId);
            return ResponseEntity.ok(asiento);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * ✅ POST /api/asientos/confirmar/{asientoId}
     * Confirma la reserva de un asiento (lo marca como OCUPADO)
     */
    @PostMapping("/confirmar/{asientoId}")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long asientoId) {
        try {
            Asiento asiento = asientoService.confirmarReserva(asientoId);
            return ResponseEntity.ok(asiento);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 🔓 POST /api/asientos/liberar/{asientoId}
     * Libera un asiento reservado (vuelve a DISPONIBLE)
     */
    @PostMapping("/liberar/{asientoId}")
    public ResponseEntity<?> liberarAsiento(@PathVariable Long asientoId) {
        try {
            Asiento asiento = asientoService.liberarAsiento(asientoId);
            return ResponseEntity.ok(asiento);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 🏗️ POST /api/asientos/generar/{funcionId}
     * Genera todos los asientos para una función
     */
    @PostMapping("/generar/{funcionId}")
    public ResponseEntity<?> generarAsientosParaFuncion(@PathVariable Long funcionId) {
        try {
            List<Asiento> asientos = asientoService.generarAsientosParaFuncion(funcionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(asientos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * ✔️ GET /api/asientos/disponible/{funcionId}/{fila}/{numero}
     * Verifica si un asiento específico está disponible
     */
    @GetMapping("/disponible/{funcionId}/{fila}/{numero}")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable Long funcionId,
            @PathVariable String fila,
            @PathVariable Integer numero) {
        boolean disponible = asientoService.verificarDisponibilidad(funcionId, fila, numero);
        return ResponseEntity.ok(disponible);
    }

    /**
     * 🎭 GET /api/asientos/funcion/{funcionId}/estado/{estado}
     * Obtiene asientos de una función filtrados por estado
     */
    @GetMapping("/funcion/{funcionId}/estado/{estado}")
    public ResponseEntity<List<Asiento>> obtenerAsientosPorEstado(
            @PathVariable Long funcionId,
            @PathVariable EstadoAsiento estado) {
        List<Asiento> asientos = asientoService.obtenerAsientosPorEstado(funcionId, estado);
        return ResponseEntity.ok(asientos);
    }

    /**
     * 🎫 GET /api/asientos/funcion/{funcionId}/tipo/{tipo}
     * Obtiene asientos de una función filtrados por tipo (VIP, NORMAL, etc.)
     */
    @GetMapping("/funcion/{funcionId}/tipo/{tipo}")
    public ResponseEntity<List<Asiento>> obtenerAsientosPorTipo(
            @PathVariable Long funcionId,
            @PathVariable TipoAsiento tipo) {
        List<Asiento> asientos = asientoService.obtenerAsientosPorTipo(funcionId, tipo);
        return ResponseEntity.ok(asientos);
    }

    /**
     * 📊 GET /api/asientos/estadisticas/{funcionId}
     * Obtiene estadísticas de ocupación de una función
     */
    @GetMapping("/estadisticas/{funcionId}")
    public ResponseEntity<EstatisticasAsientos> obtenerEstadisticas(@PathVariable Long funcionId) {
        EstatisticasAsientos estadisticas = asientoService.obtenerEstadisticas(funcionId);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * DTO para respuestas de error
     */
    private record ErrorResponse(String mensaje) {}
}
