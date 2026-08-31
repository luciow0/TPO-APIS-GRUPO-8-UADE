package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.marketplace.entity.Vehiculo;

public interface VehiculoService {
    Page<Vehiculo> listar(Pageable pageable);
    Optional<Vehiculo> buscarPorId(Long id);
    Optional<Vehiculo> buscarPorPatente(String patente);
    Vehiculo guardar(Vehiculo vehiculo);
    void eliminar(Long id);
}
