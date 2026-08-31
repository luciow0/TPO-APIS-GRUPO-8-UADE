package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.repository.UbicacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Override
    public Ubicacion updateUbicacion(Long idUbicacion, Ubicacion nuevosDatos) {

        Ubicacion ubicacionExistente = ubicacionRepository.findById(idUbicacion)
                .orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada"));

        ubicacionExistente.setDireccion(nuevosDatos.getDireccion());
        ubicacionExistente.setCiudad(nuevosDatos.getCiudad());
        ubicacionExistente.setProvincia(nuevosDatos.getProvincia());
        ubicacionExistente.setCodigoPostal(nuevosDatos.getCodigoPostal());
        ubicacionExistente.setLocalidad(nuevosDatos.getLocalidad());

        return ubicacionRepository.save(ubicacionExistente);
    }
}