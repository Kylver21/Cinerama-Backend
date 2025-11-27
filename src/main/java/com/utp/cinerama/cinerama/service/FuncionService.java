package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Funcion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FuncionService {
    List<Funcion> obtenerTodasLasFunciones();
    Optional<Funcion> obtenerFuncionPorId(Long id);
    Funcion crearFuncion(Funcion funcion);
    Funcion actualizarFuncion(Long id, Funcion funcion);
    void eliminarFuncion(Long id);
    
    // Nuevos métodos para consultas específicas
    List<Funcion> obtenerFuncionesPorPelicula(Long peliculaId);
    List<Funcion> obtenerFuncionesPorSala(Long salaId);
    List<Funcion> obtenerFuncionesPorFecha(LocalDate fecha);
    List<Funcion> obtenerFuncionesDisponibles(); // Solo funciones futuras
    List<Funcion> obtenerFuncionesDisponiblesPorPelicula(Long peliculaId); // Funciones futuras de una película
}