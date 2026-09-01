package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Ubicacion;

public interface UbicacionService {
    Ubicacion updateUbicacion(Long idUbicacion, Ubicacion nuevosDatos);
}