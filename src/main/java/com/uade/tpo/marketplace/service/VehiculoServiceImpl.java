package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.dto.VehiculoDTO;
import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.repository.VehiculoRepository;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final TipoVehiculoService tipoVehiculoService;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository,
            TipoVehiculoService tipoVehiculoService) {
        this.vehiculoRepository = vehiculoRepository;
        this.tipoVehiculoService = tipoVehiculoService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculoDTO> listar(Pageable pageable) {
        return vehiculoRepository.findAll(pageable).map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDTO buscarPorId(Long id) {
        return convertirADTO(obtenerVehiculo(id));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDTO buscarPorPatente(String patente) {
        Vehiculo vehiculo = vehiculoRepository.findByPatente(patente)
                .orElseThrow(() -> new EntityNotFoundException("Vehiculo con patente " + patente + " no encontrado"));
        return convertirADTO(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehiculo> obtenerVehiculoPorId(Long id) {
        return vehiculoRepository.findById(id);
    }

    @Override
    @Transactional
    public VehiculoDTO guardar(Vehiculo vehiculo) {
        vehiculo.setTipoVehiculo(resolverTipoVehiculo(vehiculo.getTipoVehiculo()));
        return convertirADTO(vehiculoRepository.save(vehiculo));
    }

    @PreAuthorize("@seguridadDominio.esDueñoDeVehiculo(authentication, #id)")
    @Override
    @Transactional
    public VehiculoDTO actualizar(Long id, Vehiculo vehiculo) {
        Vehiculo vehiculoExistente = obtenerVehiculo(id);

        vehiculoExistente.setPatente(vehiculo.getPatente());
        vehiculoExistente.setMarca(vehiculo.getMarca());
        vehiculoExistente.setModelo(vehiculo.getModelo());
        vehiculoExistente.setAnio(vehiculo.getAnio());
        vehiculoExistente.setColor(vehiculo.getColor());
        vehiculoExistente.setCantidadAsientos(vehiculo.getCantidadAsientos());
        vehiculoExistente.setPropietario(vehiculo.getPropietario());
        vehiculoExistente.setTipoVehiculo(resolverTipoVehiculo(vehiculo.getTipoVehiculo()));

        return convertirADTO(vehiculoRepository.save(vehiculoExistente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        vehiculoRepository.delete(obtenerVehiculo(id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePatente(String patente) {
        return vehiculoRepository.existsByPatente(patente);
    }

    private Vehiculo obtenerVehiculo(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehiculo no encontrado con id: " + id));
    }

    //
     //Valida el tipo de vehiculo que viene en el request.
     //buscamos ese id en la BD.
     // si no viene tipo (o sin id) -> el vehiculo queda sin tipo (esta permitido)
     // si el id existe -> devolvemos el tipo real de la BD
     // si el id NO existe -> 404 limpio
     //
    private TipoVehiculo resolverTipoVehiculo(TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo == null || tipoVehiculo.getIdTipoVehiculo() == null) {
            return null;
        }
        return tipoVehiculoService.obtenerTipoVehiculoPorId(tipoVehiculo.getIdTipoVehiculo())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de vehiculo no encontrado con id: " + tipoVehiculo.getIdTipoVehiculo()));
    }

    private VehiculoDTO convertirADTO(Vehiculo vehiculo) {
        return new VehiculoDTO(
                vehiculo.getIdVehiculo(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getColor(),
                vehiculo.getCantidadAsientos(),
                vehiculo.getPropietario() != null ? vehiculo.getPropietario().getIdUsuario() : null,
                vehiculo.getTipoVehiculo() != null ? vehiculo.getTipoVehiculo().getIdTipoVehiculo() : null,
                vehiculo.getTipoVehiculo() != null ? vehiculo.getTipoVehiculo().getNombre() : null);
    }
}
