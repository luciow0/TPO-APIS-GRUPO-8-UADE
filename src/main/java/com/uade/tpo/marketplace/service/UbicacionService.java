package com.uade.tpo.marketplace.service;

import java.util.Optional;

import com.uade.tpo.marketplace.entity.Ubicacion;

public interface UbicacionService {

    Optional<Ubicacion> obtenerUbicacionPorId(Long id);
}