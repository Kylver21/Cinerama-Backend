package com.utp.cinerama.cinerama.service.impl;

import com.utp.cinerama.cinerama.model.Boleto;
import com.utp.cinerama.cinerama.repository.BoletoRepository;
import com.utp.cinerama.cinerama.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class BoletoServiceImpl implements BoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    @Override
    public List<Boleto> obtenerTodosLosBoletos() {
        return boletoRepository.findAll();
    }

    @Override
    public Optional<Boleto> obtenerBoletoPorId(Long id) {
        return boletoRepository.findById(id);
    }

    @Override
    public Boleto crearBoleto(Boleto boleto) {
        return boletoRepository.save(boleto);
    }

    @Override
    public Boleto actualizarBoleto(Long id, Boleto boleto) {
        return boletoRepository.findById(id)
                .map(b -> {
                    b.setFuncion(boleto.getFuncion());
                    b.setAsiento(boleto.getAsiento());
                    b.setPrecio(boleto.getPrecio());
                    b.setEstado(boleto.getEstado());
                    b.setCliente(boleto.getCliente());
                    return boletoRepository.save(b);
                })
                .orElseThrow(() -> new RuntimeException("Boleto no encontrado"));
    }

    @Override
    public void eliminarBoleto(Long id) {
        boletoRepository.deleteById(id);
    }

    @Override
    public List<Boleto> buscarPorCliente(Long clienteId) {
        // Historial de compras: solo boletos efectivamente comprados
        return boletoRepository.findByClienteIdAndEstadoIn(
                clienteId,
                Arrays.asList(Boleto.EstadoBoleto.PAGADO, Boleto.EstadoBoleto.USADO)
        );
    }

    @Override
    public List<Boleto> buscarPorFuncion(Long funcionId) {
        return boletoRepository.findByFuncionId(funcionId);
    }

    @Override
    public List<Boleto> buscarPorEstado(Boleto.EstadoBoleto estado) {
    return boletoRepository.findByEstado(estado);
    }

}