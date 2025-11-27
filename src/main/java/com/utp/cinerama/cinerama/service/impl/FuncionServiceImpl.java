package com.utp.cinerama.cinerama.service.impl;

import com.utp.cinerama.cinerama.exception.BusinessException;
import com.utp.cinerama.cinerama.model.Funcion;
import com.utp.cinerama.cinerama.repository.AsientoRepository;
import com.utp.cinerama.cinerama.repository.BoletoRepository;
import com.utp.cinerama.cinerama.repository.FuncionRepository;
import com.utp.cinerama.cinerama.service.AsientoService;
import com.utp.cinerama.cinerama.service.FuncionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FuncionServiceImpl implements FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;
    
    @Autowired
    private AsientoRepository asientoRepository;
    
    @Autowired
    private BoletoRepository boletoRepository;
    
    @Autowired
    @Lazy // Evitar dependencia circular
    private AsientoService asientoService;

    private static final int BUFFER_LIMPIEZA_MINUTOS = 5;

    @Override
    public List<Funcion> obtenerTodasLasFunciones() {
        return funcionRepository.findAll();
    }

    @Override
    public Optional<Funcion> obtenerFuncionPorId(Long id) {
        return funcionRepository.findById(id);
    }

    @Override
    @Transactional
    public Funcion crearFuncion(Funcion funcion) {
        // Validar colisiones de horarios antes de crear
        validarColisionesHorarios(funcion);
        
        // Guardar la función primero
        Funcion funcionCreada = funcionRepository.save(funcion);
        
        // ⭐ GENERAR ASIENTOS AUTOMÁTICAMENTE
        asientoService.generarAsientosParaFuncion(funcionCreada.getId());
        
        return funcionCreada;
    }

    @Override
    public Funcion actualizarFuncion(Long id, Funcion funcion) {
        return funcionRepository.findById(id)
                .map(f -> {
                    f.setPelicula(funcion.getPelicula());
                    f.setSala(funcion.getSala());
                    f.setFechaHora(funcion.getFechaHora());
                    f.setAsientosDisponibles(funcion.getAsientosDisponibles());
                    f.setAsientosTotales(funcion.getAsientosTotales());
                    f.setPrecioEntrada(funcion.getPrecioEntrada());
                    return funcionRepository.save(f);
                })
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));
    }

    @Override
    @Transactional
    public void eliminarFuncion(Long id) {
        log.info("Eliminando función ID: {} con todos sus datos relacionados", id);
        
        // Verificar que la función existe
        if (!funcionRepository.existsById(id)) {
            throw new BusinessException("La función con ID " + id + " no existe");
        }
        
        // 1. Eliminar boletos asociados a la función
        boletoRepository.deleteByFuncionId(id);
        log.info("Boletos de la función {} eliminados", id);
        
        // 2. Eliminar asientos asociados a la función
        asientoRepository.deleteByFuncionId(id);
        log.info("Asientos de la función {} eliminados", id);
        
        // 3. Finalmente eliminar la función
        funcionRepository.deleteById(id);
        log.info("Función {} eliminada exitosamente", id);
    }
    
    @Override
    public List<Funcion> obtenerFuncionesPorPelicula(Long peliculaId) {
        return funcionRepository.findByPeliculaId(peliculaId);
    }
    
    @Override
    public List<Funcion> obtenerFuncionesPorSala(Long salaId) {
        return funcionRepository.findBySalaId(salaId);
    }
    
    @Override
    public List<Funcion> obtenerFuncionesPorFecha(LocalDate fecha) {
        return funcionRepository.findByFechaOrdenado(fecha);
    }
    
    @Override
    public List<Funcion> obtenerFuncionesDisponibles() {
        return funcionRepository.findFuncionesDisponibles(LocalDateTime.now());
    }
    
    @Override
    public List<Funcion> obtenerFuncionesDisponiblesPorPelicula(Long peliculaId) {
        return funcionRepository.findFuncionesDisponiblesByPeliculaId(peliculaId, LocalDateTime.now());
    }

    /**
     * Valida que no haya colisiones de horarios en la misma sala
     * Considera la duración de la película + buffer de limpieza
     */
    private void validarColisionesHorarios(Funcion nuevaFuncion) {
        // Si la película no tiene duración definida, usar duración promedio (120 minutos)
        Integer duracion = nuevaFuncion.getPelicula().getDuracion() != null ? 
                          nuevaFuncion.getPelicula().getDuracion() : 120;
        
        // Calcular hora de inicio y fin de la nueva función (incluyendo buffer de limpieza)
        LocalDateTime inicioNueva = nuevaFuncion.getFechaHora();
        LocalDateTime finNueva = inicioNueva.plusMinutes(duracion + BUFFER_LIMPIEZA_MINUTOS);
        
        // Obtener todas las funciones de la misma sala
        List<Funcion> funcionesExistentes = funcionRepository.findBySalaId(nuevaFuncion.getSala().getId());
        
        for (Funcion funcionExistente : funcionesExistentes) {
            // Saltar si es la misma función (caso de actualización)
            if (nuevaFuncion.getId() != null && nuevaFuncion.getId().equals(funcionExistente.getId())) {
                continue;
            }
            
            Integer duracionExistente = funcionExistente.getPelicula().getDuracion() != null ? 
                                       funcionExistente.getPelicula().getDuracion() : 120;
            
            LocalDateTime inicioExistente = funcionExistente.getFechaHora();
            LocalDateTime finExistente = inicioExistente.plusMinutes(duracionExistente + BUFFER_LIMPIEZA_MINUTOS);
            
            // Verificar solapamiento
            boolean haySolapamiento = inicioNueva.isBefore(finExistente) && finNueva.isAfter(inicioExistente);
            
            if (haySolapamiento) {
                throw new BusinessException(
                    String.format("Colisión de horarios detectada en la sala %s. " +
                                "La función a las %s terminaría a las %s, " +
                                "pero ya existe una función a las %s que ocupa la sala hasta las %s.",
                                nuevaFuncion.getSala().getNombre(),
                                inicioNueva,
                                finNueva,
                                inicioExistente,
                                finExistente)
                );
            }
        }
    }
}