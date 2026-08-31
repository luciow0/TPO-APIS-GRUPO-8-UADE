package com.uade.tpo.marketplace.service;

import java.util.Optional;

import com.uade.tpo.marketplace.entity.Vehiculo;

public interface VehiculoService {

    Optional<Vehiculo> obtenerVehiculoPorId(Long id);
}