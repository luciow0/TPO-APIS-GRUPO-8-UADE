package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import java.util.List;
import java.util.Optional;

public interface TipoVehiculoService {
    List<TipoVehiculo> getTiposVehiculo();
    Optional<TipoVehiculo> obtenerTipoVehiculoPorId(Long idTipoVehiculo);
    TipoVehiculo crearTipoVehiculo(TipoVehiculo tipoVehiculo);
}