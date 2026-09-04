package com.uade.tpo.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.repository.TipoVehiculoRepository;

@Service
public class TipoVehiculoServiceImpl implements TipoVehiculoService {

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    @Override
    public List<TipoVehiculo> getTiposVehiculo() {
        return tipoVehiculoRepository.findAll();
    }

    @Override
    @Transactional
    public TipoVehiculo crearTipoVehiculo(TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo.getNombre() == null || tipoVehiculo.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de vehiculo es requerido");
        }
        // No permitir dos tipos con el mismo nombre
        if (tipoVehiculoRepository.existsByNombre(tipoVehiculo.getNombre())) {
            throw new IllegalArgumentException(
                    "El tipo de vehiculo '" + tipoVehiculo.getNombre() + "' ya existe");
        }
        return tipoVehiculoRepository.save(tipoVehiculo);
    }
}
