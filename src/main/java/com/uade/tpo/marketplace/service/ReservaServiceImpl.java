package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.repository.ReservaRepository;
import com.uade.tpo.marketplace.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @PreAuthorize("@seguridadDominio.esDueñoDeReserva(authentication, #idReserva)")
    @Override
    public Reserva cancelarReserva(Long idReserva, Long idUsuario)throws ReservaNotFoundException, ReservaInvalidException {
        Optional<Reserva> reservaOptional = reservaRepository.findById(idReserva);

    if (reservaOptional.isEmpty()) {
        throw new ReservaNotFoundException();
    }

    Reserva reserva = reservaOptional.get();

    if (!reserva.getCliente().getIdUsuario().equals(idUsuario)) {
        throw new ReservaInvalidException();
    }

    if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
        throw new ReservaInvalidException();
    }

    if (!reserva.getFechaInicio().isAfter(LocalDate.now())) {
        throw new ReservaInvalidException();
    }

    reserva.setEstado(EstadoReserva.CANCELADA);

    return reservaRepository.save(reserva);
    }

   @Override
    public Reserva confirmarReserva(Long idReserva)throws ReservaNotFoundException, ReservaInvalidException {

        Optional<Reserva> reservaOptional =
            reservaRepository.findById(idReserva);

        if (reservaOptional.isEmpty()) {
            throw new ReservaNotFoundException();
        }

        Reserva reserva = reservaOptional.get();

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReservaInvalidException();
        }

        validarSolapamiento(
            reserva.getPublicacion().getIdPublicacion(),
            reserva.getFechaInicio(),
            reserva.getFechaFin(),
            reserva.getIdReserva()
        );

        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return reservaRepository.save(reserva);
    }

    @Override
    public Reserva rechazarReserva(Long idReserva)
            throws ReservaNotFoundException, ReservaInvalidException {

        Optional<Reserva> reservaOptional =
            reservaRepository.findById(idReserva);

        if (reservaOptional.isEmpty()) {
            throw new ReservaNotFoundException();
        }

        Reserva reserva = reservaOptional.get();

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReservaInvalidException();
        }

        reserva.setEstado(EstadoReserva.RECHAZADA);

        return reservaRepository.save(reserva);
    }
    
    @Override
    public Reserva crearReservaDesdeCarrito(Carrito carrito)
            throws ReservaInvalidException {

        if (carrito == null
                || carrito.getUsuario() == null
                || carrito.getPublicacion() == null
                || carrito.getFechaInicio() == null
                || carrito.getFechaFin() == null
                || carrito.getFechaExpiracion() == null
                || carrito.getPrecioDiaAplicado() == null) {

            throw new ReservaInvalidException();
        }

        if (!carrito.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            throw new ReservaInvalidException();
        }

        boolean tieneReservaPendiente =
                reservaRepository.existsByClienteIdUsuarioAndEstado(
                        carrito.getUsuario().getIdUsuario(),
                        EstadoReserva.PENDIENTE);

        if (tieneReservaPendiente) {
            throw new ReservaInvalidException();
        }

        validarFechas(
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        validarSolapamiento(
                carrito.getPublicacion().getIdPublicacion(),
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        Reserva reserva = new Reserva();

        reserva.setFechaInicio(carrito.getFechaInicio());
        reserva.setFechaFin(carrito.getFechaFin());
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioDiaAplicado(carrito.getPrecioDiaAplicado());
        reserva.setCliente(carrito.getUsuario());
        reserva.setPublicacion(carrito.getPublicacion());

        return reservaRepository.save(reserva);
    }

    @Override
    @PreAuthorize("@seguridadDominio.esDueñoDeReserva(authentication, #idReserva) or hasRole('ADMIN')")
    public Optional<Reserva> getReservaById(Long idReserva) {
        return reservaRepository.findById(idReserva);
    }

    @PreAuthorize("@seguridadDominio.esMismoUsuario(authentication, #idUsuario)")
    @Override
    public Page<Reserva> getReservasByUsuario(Long idUsuario, PageRequest pageRequest) {
        
        return reservaRepository.findByClienteIdUsuario(idUsuario, pageRequest);
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin)throws ReservaInvalidException {

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new ReservaInvalidException();
        }
    }

    private long calcularCantidadDias(LocalDate fechaInicio, LocalDate fechaFin) {
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    @Override
    public void validarSolapamiento(Long idPublicacion,LocalDate fechaInicio,LocalDate fechaFin) throws ReservaInvalidException {

        validarSolapamiento(
                idPublicacion,
                fechaInicio,
                fechaFin,
                null);
    }

    private void validarSolapamiento(
            Long idPublicacion,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idReservaIgnorada)
            throws ReservaInvalidException {

        List<Reserva> reservasBloqueantes =
            new ArrayList<>(
                reservaRepository.findByPublicacionIdPublicacionAndEstado(
                        idPublicacion,
                        EstadoReserva.PENDIENTE
                )
            );

        reservasBloqueantes.addAll(
            reservaRepository.findByPublicacionIdPublicacionAndEstado(
                    idPublicacion,
                    EstadoReserva.CONFIRMADA
            )
        );

        LocalDate finBloqueadoNuevaReserva = fechaFin.plusDays(1);

        for (Reserva reservaExistente : reservasBloqueantes) {

            if (idReservaIgnorada != null
                    && reservaExistente.getIdReserva()
                            .equals(idReservaIgnorada)) {
                continue;
            }

            LocalDate finBloqueadoExistente =
                reservaExistente.getFechaFin().plusDays(1);

            boolean haySolapamiento =
                fechaInicio.isBefore(finBloqueadoExistente)
                && reservaExistente.getFechaInicio()
                        .isBefore(finBloqueadoNuevaReserva);

            if (haySolapamiento) {
                throw new ReservaInvalidException();
            }
        }
    }
    
}
