package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.TipoVehiculo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TipoVehiculoService {

    public Page<TipoVehiculo> getTipoVehiculo(PageRequest pageRequest);

}
