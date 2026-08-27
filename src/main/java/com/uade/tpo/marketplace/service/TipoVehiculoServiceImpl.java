package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.repository.TipoVehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TipoVehiculoServiceImpl implements TipoVehiculoService {

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    public Page<TipoVehiculo> getTipoVehiculo(PageRequest pageable) {
        return tipoVehiculoRepository.findAll(pageable);
    }

}
