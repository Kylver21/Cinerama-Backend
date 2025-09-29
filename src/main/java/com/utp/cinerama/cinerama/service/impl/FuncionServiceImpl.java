package com.utp.cinerama.cinerama.service.impl;

import com.utp.cinerama.cinerama.model.Funcion;
import com.utp.cinerama.cinerama.repository.FuncionRepository;
import com.utp.cinerama.cinerama.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionServiceImpl implements FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;

    @Override
    public List<Funcion> obtenerTodasLasFunciones() {
        return funcionRepository.findAll();
    }

    @Override
    public Optional<Funcion> obtenerFuncionPorId(Long id) {
        return funcionRepository.findById(id);
    }

    @Override
    public Funcion crearFuncion(Funcion funcion) {
        return funcionRepository.save(funcion);
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
                    return funcionRepository.save(f);
                })
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));
    }

    @Override
    public void eliminarFuncion(Long id) {
        funcionRepository.deleteById(id);
    }
}