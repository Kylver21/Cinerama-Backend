package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Funcion;

import java.util.List;
import java.util.Optional;

public interface FuncionService {
    List<Funcion> obtenerTodasLasFunciones();
    Optional<Funcion> obtenerFuncionPorId(Long id);
    Funcion crearFuncion(Funcion funcion);
    Funcion actualizarFuncion(Long id, Funcion funcion);
    void eliminarFuncion(Long id);
}