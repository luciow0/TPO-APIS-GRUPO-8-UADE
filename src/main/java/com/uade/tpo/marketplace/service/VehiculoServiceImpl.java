package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uade.tpo.marketplace.dto.VehiculoDTO;
import com.uade.tpo.marketplace.dto.VehiculoRequest;
import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import com.uade.tpo.marketplace.repository.VehiculoRepository;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final TipoVehiculoService tipoVehiculoService;
    private final UsuarioRepository usuarioRepository;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository,
            TipoVehiculoService tipoVehiculoService,
            UsuarioRepository usuarioRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.tipoVehiculoService = tipoVehiculoService;
        this.usuarioRepository = usuarioRepository;
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
    @PreAuthorize("@seguridadDominio.esMismoUsuario(authentication, #request.idUsuario) or hasRole('ADMIN')")
    public VehiculoDTO guardar(VehiculoRequest request) {
        Usuario propietario = obtenerUsuario(request.getIdUsuario());

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(request.getPatente());
        vehiculo.setMarca(request.getMarca());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setColor(request.getColor());
        vehiculo.setCantidadAsientos(request.getCantidadAsientos());
        vehiculo.setPropietario(propietario);
        vehiculo.setTipoVehiculo(resolverTipoVehiculo(request.getIdTipoVehiculo()));

        return convertirADTO(vehiculoRepository.save(vehiculo));
    }

    @PreAuthorize("@seguridadDominio.esDuenioDeVehiculo(authentication, #id) or hasRole('ADMIN')")
    @Override
    @Transactional
    public VehiculoDTO actualizar(Long id, VehiculoRequest request) {
        Vehiculo vehiculoExistente = obtenerVehiculo(id);

        vehiculoExistente.setPatente(request.getPatente());
        vehiculoExistente.setMarca(request.getMarca());
        vehiculoExistente.setModelo(request.getModelo());
        vehiculoExistente.setAnio(request.getAnio());
        vehiculoExistente.setColor(request.getColor());
        vehiculoExistente.setCantidadAsientos(request.getCantidadAsientos());
        vehiculoExistente.setTipoVehiculo(resolverTipoVehiculo(request.getIdTipoVehiculo()));

        return convertirADTO(vehiculoRepository.save(vehiculoExistente));
    }

    @Override
    @Transactional
    @PreAuthorize("@seguridadDominio.esDuenioDeVehiculo(authentication, #id) or hasRole('ADMIN')")
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

    private Usuario obtenerUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + idUsuario));
    }

    //
     //Valida el tipo de vehiculo que viene en el request.
     //buscamos ese id en la BD.
     // si no viene tipo (o sin id) -> el vehiculo queda sin tipo (esta permitido)
     // si el id existe -> devolvemos el tipo real de la BD
     // si el id NO existe -> 404 limpio
     //
    private TipoVehiculo resolverTipoVehiculo(Long idTipoVehiculo) {
        if (idTipoVehiculo == null) {
            return null;
        }
        return tipoVehiculoService.obtenerTipoVehiculoPorId(idTipoVehiculo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de vehiculo no encontrado con id: " + idTipoVehiculo));
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
