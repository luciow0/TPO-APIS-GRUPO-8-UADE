package com.uade.tpo.marketplace.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.Enum.RolResena;
import com.uade.tpo.marketplace.dto.ResenaPendienteResponse;
import com.uade.tpo.marketplace.dto.ResenaPublicaResponse;
import com.uade.tpo.marketplace.dto.ResenaRequest;
import com.uade.tpo.marketplace.dto.ResenaResponse;
import com.uade.tpo.marketplace.dto.ResenasDeReservaResponse;
import com.uade.tpo.marketplace.dto.UsuarioResumen;
import com.uade.tpo.marketplace.entity.Resena;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.exception.DatoInvalidoException;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.repository.ResenaRepository;
import com.uade.tpo.marketplace.repository.ReservaRepository;

// Acá están todas las reglas de las reseñas: quién califica, el plazo y el doble ciego.
@Service
public class ResenaServiceImpl implements ResenaService {

    private static final int DIAS_PLAZO = 7;
    private static final int MAX_COMENTARIO = 500;

    private final ResenaRepository resenaRepository;
    private final ReservaRepository reservaRepository;

    public ResenaServiceImpl(ResenaRepository resenaRepository, ReservaRepository reservaRepository) {
        this.resenaRepository = resenaRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional
    public ResenaResponse crear(String emailAutor, ResenaRequest request) throws ReservaNotFoundException {
        if (request == null || request.getIdReserva() == null) {
            throw new DatoInvalidoException("Elegí la reserva que querés calificar.");
        }
        validarContenido(request);

        Reserva reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(ReservaNotFoundException::new);

        Usuario conductor = reserva.getCliente();
        Usuario propietario = propietarioDe(reserva);
        boolean esConductor = conductor.getEmail().equals(emailAutor);
        boolean esPropietario = propietario.getEmail().equals(emailAutor);
        if (!esConductor && !esPropietario) {
            throw new AccessDeniedException("No participaste de esta reserva.");
        }

        if (reserva.getEstado() != EstadoReserva.FINALIZADA || plazoVencido(reserva)) {
            throw new IllegalStateException(reserva.getEstado() != EstadoReserva.FINALIZADA
                    ? "Solo podés calificar una reserva finalizada."
                    : "Ya pasaron los " + DIAS_PLAZO + " días para calificar esta reserva.");
        }
        if (resenaRepository.existsByReservaIdReservaAndAutorEmail(reserva.getIdReserva(), emailAutor)) {
            throw new IllegalStateException("Ya calificaste esta reserva.");
        }

        Resena resena = new Resena();
        resena.setReserva(reserva);
        resena.setAutor(esConductor ? conductor : propietario);
        resena.setDestinatario(esConductor ? propietario : conductor);
        resena.setRol(esConductor ? RolResena.PROPIETARIO : RolResena.CONDUCTOR);
        resena.setEstrellas(request.getEstrellas());
        resena.setComentario(limpiar(request.getComentario()));
        resena.setFecha(LocalDateTime.now());

        try {
            return aResponse(resenaRepository.saveAndFlush(resena));
        } catch (DataIntegrityViolationException e) {
            // Dos pedidos a la vez: la restricción UNIQUE (reserva, autor) frena al segundo.
            throw new IllegalStateException("Ya calificaste esta reserva.");
        }
    }

    @Override
    @Transactional
    public ResenaResponse editar(String emailAutor, Long idResena, ResenaRequest request) {
        validarContenido(request);

        Resena resena = resenaRepository.findById(idResena)
                .orElseThrow(() -> new EntityNotFoundException("No encontramos esa reseña."));

        if (!resena.getAutor().getEmail().equals(emailAutor)) {
            throw new AccessDeniedException("La reseña es de otra persona.");
        }
        if (estaPublicada(resena)) {
            throw new IllegalStateException("Esta reseña ya está publicada y no se puede editar.");
        }

        resena.setEstrellas(request.getEstrellas());
        resena.setComentario(limpiar(request.getComentario()));
        return aResponse(resenaRepository.save(resena));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaPendienteResponse> pendientes(String email) {
        return reservaRepository.findDeUsuarioPorEstado(email, EstadoReserva.FINALIZADA).stream()
                .filter(reserva -> !plazoVencido(reserva))
                .filter(reserva -> !resenaRepository
                        .existsByReservaIdReservaAndAutorEmail(reserva.getIdReserva(), email))
                .map(reserva -> aPendiente(reserva, email))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponse> mias(String email) {
        return resenaRepository.findByAutorEmailOrderByFechaDesc(email).stream()
                .map(this::aResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResenasDeReservaResponse deReserva(String email, Long idReserva) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(ReservaNotFoundException::new);

        if (!reserva.getCliente().getEmail().equals(email)
                && !propietarioDe(reserva).getEmail().equals(email)) {
            throw new AccessDeniedException("No participaste de esta reserva.");
        }

        ResenaResponse mia = resenaRepository.findByReservaIdReservaAndAutorEmail(idReserva, email)
                .map(this::aResponse)
                .orElse(null);

        var delOtro = resenaRepository.findByReservaIdReservaAndDestinatarioEmail(idReserva, email);
        ResenaPublicaResponse deLaOtraParte = delOtro
                .filter(this::estaPublicada)
                .map(this::aPublica)
                .orElse(null);

        return new ResenasDeReservaResponse(mia, deLaOtraParte, delOtro.isPresent());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaPublicaResponse> publicadasDeUsuario(Long idUsuario, RolResena rol) {
        if (rol == null) {
            throw new DatoInvalidoException("Indicá si querés las reseñas como PROPIETARIO o como CONDUCTOR.");
        }
        return resenaRepository.findByDestinatarioIdUsuarioAndRolOrderByFechaDesc(idUsuario, rol).stream()
                .filter(this::estaPublicada)
                .map(this::aPublica)
                .toList();
    }

    // Publicada = la otra parte también calificó esa reserva, o ya pasó el plazo.
    private boolean estaPublicada(Resena resena) {
        return plazoVencido(resena.getReserva())
                || resenaRepository.existsByReservaIdReservaAndIdResenaNot(
                        resena.getReserva().getIdReserva(), resena.getIdResena());
    }

    private boolean plazoVencido(Reserva reserva) {
        return reserva.getFechaFinalizacion() != null
                && LocalDateTime.now().isAfter(reserva.getFechaFinalizacion().plusDays(DIAS_PLAZO));
    }

    private Usuario propietarioDe(Reserva reserva) {
        return reserva.getPublicacion().getVehiculo().getPropietario();
    }

    private void validarContenido(ResenaRequest request) {
        if (request == null || request.getEstrellas() == null
                || request.getEstrellas() < 1 || request.getEstrellas() > 5) {
            throw new DatoInvalidoException("Elegí de 1 a 5 estrellas.");
        }
        if (request.getComentario() != null && request.getComentario().length() > MAX_COMENTARIO) {
            throw new DatoInvalidoException("El comentario puede tener hasta " + MAX_COMENTARIO + " caracteres.");
        }
    }

    private String limpiar(String comentario) {
        return (comentario == null || comentario.isBlank()) ? null : comentario.trim();
    }

    private ResenaResponse aResponse(Resena r) {
        return new ResenaResponse(
                r.getIdResena(),
                r.getReserva().getIdReserva(),
                r.getAutor().getIdUsuario(),
                r.getDestinatario().getIdUsuario(),
                r.getRol(),
                r.getEstrellas(),
                r.getComentario(),
                r.getFecha(),
                estaPublicada(r),
                UsuarioResumen.de(r.getDestinatario()));
    }

    private ResenaPublicaResponse aPublica(Resena r) {
        return new ResenaPublicaResponse(
                r.getIdResena(),
                r.getReserva().getIdReserva(),
                r.getRol(),
                r.getEstrellas(),
                r.getComentario(),
                r.getFecha(),
                UsuarioResumen.de(r.getAutor()));
    }

    private ResenaPendienteResponse aPendiente(Reserva reserva, String email) {
        boolean soyConductor = reserva.getCliente().getEmail().equals(email);
        Usuario otraParte = soyConductor ? propietarioDe(reserva) : reserva.getCliente();
        Vehiculo v = reserva.getPublicacion().getVehiculo();

        return new ResenaPendienteResponse(
                reserva.getIdReserva(),
                new ResenaPendienteResponse.VehiculoResumen(
                        v.getIdVehiculo(), v.getMarca(), v.getModelo(), v.getAnio()),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getFechaFinalizacion(),
                UsuarioResumen.de(otraParte),
                soyConductor ? RolResena.PROPIETARIO : RolResena.CONDUCTOR);
    }
}
