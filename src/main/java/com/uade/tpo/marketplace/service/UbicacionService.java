package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;
import com.uade.tpo.marketplace.dto.UbicacionRequest;
import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.exception.UbicacionNotFoundException;

public interface UbicacionService {
    Ubicacion crear(UbicacionRequest request);
    List<Ubicacion> listar();
    Optional<Ubicacion> obtenerUbicacionPorId(Long id);
    Ubicacion updateUbicacion(Long id, UbicacionRequest req)
            throws UbicacionNotFoundException;
    void eliminar(Long id);
}
