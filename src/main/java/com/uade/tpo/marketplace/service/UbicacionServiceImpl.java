package com.uade.tpo.marketplace.service;

import java.util.Optional;
import java.util.List;

import com.uade.tpo.marketplace.dto.UbicacionRequest;
import com.uade.tpo.marketplace.exception.UbicacionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.repository.UbicacionRepository;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Optional<Ubicacion> obtenerUbicacionPorId(Long idUbicacion) {
        return ubicacionRepository.findById(idUbicacion);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Ubicacion crear(UbicacionRequest request) {
        Ubicacion u = new Ubicacion();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        u.setCreador(usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("No encontramos tu cuenta.")));
        u.setZona(request.getZona());
        u.setDireccion(request.getDireccion());
        u.setCiudad(request.getCiudad());
        u.setProvincia(request.getProvincia());
        u.setLocalidad(request.getLocalidad());
        u.setCodigoPostal(request.getCodigoPostal());
        u.setLatitud(request.getLatitud());
        u.setLongitud(request.getLongitud());
        u.setPlaceId(request.getPlaceId());
        return ubicacionRepository.save(u);   // aquí se genera el idUbicacion
    }

    @Override
    public List<Ubicacion> listar() {
        return ubicacionRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Ubicacion updateUbicacion(Long id, UbicacionRequest request)
            throws UbicacionNotFoundException {
        Ubicacion u = ubicacionRepository.findById(id)
                .orElseThrow(UbicacionNotFoundException::new);
        u.setDireccion(request.getDireccion());
        u.setCiudad(request.getCiudad());
        u.setProvincia(request.getProvincia());
        u.setLocalidad(request.getLocalidad());
        u.setCodigoPostal(request.getCodigoPostal());
        u.setZona(request.getZona());
        u.setLatitud(request.getLatitud());
        u.setLongitud(request.getLongitud());
        u.setPlaceId(request.getPlaceId());
        return ubicacionRepository.save(u);
    }
}
