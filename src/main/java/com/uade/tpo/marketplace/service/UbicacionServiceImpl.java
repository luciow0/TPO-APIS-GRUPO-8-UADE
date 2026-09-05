package com.uade.tpo.marketplace.service;

import java.util.Optional;
import java.util.List;

import com.uade.tpo.marketplace.dto.UbicacionRequest;
import com.uade.tpo.marketplace.exception.UbicacionNotFoundException;
import com.uade.tpo.marketplace.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.repository.UbicacionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Override
    public Optional<Ubicacion> obtenerUbicacionPorId(Long idUbicacion) {
        return ubicacionRepository.findById(idUbicacion);
    }

    @Override
    public Ubicacion crear(UbicacionRequest request) {
        Ubicacion u = new Ubicacion();
        u.setDireccion(request.getDireccion());
        u.setCiudad(request.getCiudad());
        u.setProvincia(request.getProvincia());
        u.setLocalidad(request.getLocalidad());
        u.setCodigoPostal(request.getCodigoPostal());
        return ubicacionRepository.save(u);   // aquí se genera el idUbicacion
    }

    @Override
    public List<Ubicacion> listar() {
        return ubicacionRepository.findAll();
    }

    @Override
    public Ubicacion updateUbicacion(Long id, UbicacionRequest request)
            throws UbicacionNotFoundException {
        Ubicacion u = ubicacionRepository.findById(id)
                .orElseThrow(UbicacionNotFoundException::new);
        u.setDireccion(request.getDireccion());
        u.setCiudad(request.getCiudad());
        u.setProvincia(request.getProvincia());
        u.setLocalidad(request.getLocalidad());
        u.setCodigoPostal(request.getCodigoPostal());
        return ubicacionRepository.save(u);
    }

    @Override
    public void eliminar(Long id) {
        // no se puede borrar una ubicación en uso (FK nullable=false)
        if (publicacionRepository.existsByUbicacion_IdUbicacion(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar: la ubicación está en uso por una publicación");
        }
        ubicacionRepository.deleteById(id);
    }

}