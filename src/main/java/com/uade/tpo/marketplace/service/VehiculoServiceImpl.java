package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uade.tpo.marketplace.auth.SeguridadDominio;
import com.uade.tpo.marketplace.dto.VehiculoDTO;
import com.uade.tpo.marketplace.dto.VehiculoRequest;
import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.exception.DatoInvalidoException;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.repository.PublicacionRepository; // NUEVO (10a)
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import com.uade.tpo.marketplace.repository.VehiculoRepository;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final TipoVehiculoService tipoVehiculoService;
    private final UsuarioRepository usuarioRepository;
    private final SeguridadDominio seguridadDominio;
    private final PublicacionRepository publicacionRepository; // NUEVO (10b)

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository,
            TipoVehiculoService tipoVehiculoService,
            UsuarioRepository usuarioRepository,
            SeguridadDominio seguridadDominio,
            PublicacionRepository publicacionRepository) { // NUEVO (10b)
        this.vehiculoRepository = vehiculoRepository;
        this.tipoVehiculoService = tipoVehiculoService;
        this.usuarioRepository = usuarioRepository;
        this.seguridadDominio = seguridadDominio;
        this.publicacionRepository = publicacionRepository; // NUEVO (10b)
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculoDTO> listar(Pageable pageable) {
        return vehiculoRepository.findAll(pageable).map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated() and #emailUsuario == authentication.name")
    public Page<VehiculoDTO> listarMisVehiculos(String emailUsuario, Pageable pageable) {
        Pageable pagina = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100),
                Sort.by(Sort.Direction.DESC, "idVehiculo"));
        if (pagina.getOffset() > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay más resultados para mostrar.");
        }
        return vehiculoRepository.findByPropietario_Email(emailUsuario, pagina).map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDTO buscarPorId(Long id) {
        return convertirADTO(obtenerVehiculo(id));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')") // Si fuera público, cualquiera podría averiguar si una patente está registrada
    public VehiculoDTO buscarPorPatente(String patente) {
        Vehiculo vehiculo = vehiculoRepository.findByPatente(patente)
                .orElseThrow(() -> new EntityNotFoundException("No encontramos un vehículo con esa patente."));
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

        // Puede dejar su propia patente; solo se rechaza si la tiene otro vehiculo.
        vehiculoRepository.findByPatente(request.getPatente())
                .filter(otro -> !otro.getIdVehiculo().equals(id))
                .ifPresent(otro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya hay un vehículo registrado con esa patente.");
                });

        vehiculoExistente.setPatente(request.getPatente());
        vehiculoExistente.setMarca(request.getMarca());
        vehiculoExistente.setModelo(request.getModelo());
        vehiculoExistente.setAnio(request.getAnio());
        vehiculoExistente.setColor(request.getColor());
        vehiculoExistente.setCantidadAsientos(request.getCantidadAsientos());
        vehiculoExistente.setTipoVehiculo(resolverTipoVehiculo(request.getIdTipoVehiculo()));

        return convertirADTO(vehiculoRepository.save(vehiculoExistente));
    }

    // NUEVO (10c): ahora verifica que el vehículo no tenga publicaciones antes de borrarlo
    @Override
    @Transactional
    @PreAuthorize("@seguridadDominio.esDuenioDeVehiculo(authentication, #id) or hasRole('ADMIN')")
    public void eliminar(Long id) {
        Vehiculo vehiculo = obtenerVehiculo(id);
        if (publicacionRepository.existsByVehiculo_IdVehiculo(id)) {
            // IllegalStateException llega al front como 409 con este mensaje
            throw new IllegalStateException(
                    "Este vehículo tiene publicaciones asociadas, por eso no se puede eliminar.");
        }
        vehiculoRepository.delete(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePatente(String patente) {
        return vehiculoRepository.existsByPatente(patente);
    }

    private Vehiculo obtenerVehiculo(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No encontramos ese vehículo."));
    }

    private Usuario obtenerUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No encontramos esa cuenta."));
    }

    //
     //Valida el tipo de vehiculo que viene en el request.
     //buscamos ese id en la BD.
     // si no viene tipo (o sin id) -> el vehiculo queda sin tipo (esta permitido)
     // si el id existe -> devolvemos el tipo real de la BD
     // si el id NO existe -> 400 (es un dato invalido del request, no un recurso pedido por URL)
     //
    private TipoVehiculo resolverTipoVehiculo(Long idTipoVehiculo) {
        if (idTipoVehiculo == null) {
            return null;
        }
        return tipoVehiculoService.obtenerTipoVehiculoPorId(idTipoVehiculo)
                .orElseThrow(() -> new DatoInvalidoException(
                        "Elegí un tipo de vehículo de la lista."));
    }

    private VehiculoDTO convertirADTO(Vehiculo vehiculo) {
        boolean verPatente = seguridadDominio.puedeVerPatente(
                SecurityContextHolder.getContext().getAuthentication(), vehiculo);

        return new VehiculoDTO(
                vehiculo.getIdVehiculo(),
                verPatente ? vehiculo.getPatente() : null,
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