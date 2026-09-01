package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.repository.VehiculoRepository;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Vehiculo> listar(Pageable pageable) {
        return vehiculoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehiculo> buscarPorId(Long id) {
        return vehiculoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehiculo> buscarPorPatente(String patente) {
        return vehiculoRepository.findByPatente(patente);
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    @Transactional
    public Vehiculo actualizar(Long id, Vehiculo vehiculo) {
        Vehiculo vehiculoExistente = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con id: " + id));

        vehiculoExistente.setPatente(vehiculo.getPatente());
        vehiculoExistente.setMarca(vehiculo.getMarca());
        vehiculoExistente.setModelo(vehiculo.getModelo());
        vehiculoExistente.setAnio(vehiculo.getAnio());
        vehiculoExistente.setColor(vehiculo.getColor());
        vehiculoExistente.setCantidadAsientos(vehiculo.getCantidadAsientos());
        vehiculoExistente.setPropietario(vehiculo.getPropietario());

        return vehiculoRepository.save(vehiculoExistente);
    }

    @Override
    public void eliminar(Long id) {
        vehiculoRepository.deleteById(id);
    }

    @Override
    public boolean existePatente(String patente) {
        return vehiculoRepository.existsByPatente(patente);
    }
}