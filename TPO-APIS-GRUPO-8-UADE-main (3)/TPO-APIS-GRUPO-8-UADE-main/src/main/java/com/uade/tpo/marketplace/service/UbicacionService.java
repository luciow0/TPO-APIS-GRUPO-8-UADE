package com.uade.tpo.marketplace.service;

import java.util.Optional;

import com.uade.tpo.marketplace.entity.Ubicacion;

public interface UbicacionService {
    Ubicacion updateUbicacion(Long idUbicacion, Ubicacion nuevosDatos);

    Optional<Ubicacion> obtenerUbicacionPorId(Long idUbicacion);
}