package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.dto.VehiculoDTO;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import com.uade.tpo.marketplace.repository.VehiculoRepository;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository, UsuarioRepository usuarioRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
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
    public Vehiculo guardar(VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = convertirAEntidad(vehiculoDTO);
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    @Transactional
    public Vehiculo actualizar(Long id, VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculoExistente = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con id: " + id));

        vehiculoExistente.setPatente(vehiculoDTO.getPatente());
        vehiculoExistente.setMarca(vehiculoDTO.getMarca());
        vehiculoExistente.setModelo(vehiculoDTO.getModelo());
        vehiculoExistente.setAnio(vehiculoDTO.getAnio());
        vehiculoExistente.setColor(vehiculoDTO.getColor());
        vehiculoExistente.setCantidadAsientos(vehiculoDTO.getCantidadAsientos());
        vehiculoExistente.setPropietario(obtenerUsuario(vehiculoDTO.getIdUsuario()));

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

    private Vehiculo convertirAEntidad(VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(vehiculoDTO.getPatente());
        vehiculo.setMarca(vehiculoDTO.getMarca());
        vehiculo.setModelo(vehiculoDTO.getModelo());
        vehiculo.setAnio(vehiculoDTO.getAnio());
        vehiculo.setColor(vehiculoDTO.getColor());
        vehiculo.setCantidadAsientos(vehiculoDTO.getCantidadAsientos());
        vehiculo.setPropietario(obtenerUsuario(vehiculoDTO.getIdUsuario()));
        return vehiculo;
    }

    private Usuario obtenerUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));
    }
}