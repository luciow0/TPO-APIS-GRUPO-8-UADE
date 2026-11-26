package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.repository.PagoRepository;
import com.uade.tpo.marketplace.repository.ReservaRepository;
import com.uade.tpo.marketplace.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @PreAuthorize("@seguridadDominio.esDuenioDeReserva(authentication, #idReserva)")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Reserva cancelarReserva(Long idReserva)throws ReservaNotFoundException, ReservaInvalidException {
        Optional<Reserva> reservaOptional = reservaRepository.findById(idReserva);

    if (reservaOptional.isEmpty()) {
        throw new ReservaNotFoundException();
    }

    // El @PreAuthorize ya verifica que la reserva sea del usuario del token.
    Reserva reserva = reservaOptional.get();

    if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
        // Todavia no se pago: se libera la reserva y el pago queda rechazado.
        pagoRepository.findByReservaIdReserva(idReserva).ifPresent(pago -> {
            pago.setEstado(EstadoPago.RECHAZADO);
            pagoRepository.save(pago);
        });
    } else if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
        throw new ReservaInvalidException(
                "Esta reserva ya está " + reserva.getEstado().name().toLowerCase()
                        + " y no se puede cancelar.");
    } else if (!reserva.getFechaInicio().isAfter(LocalDate.now())) {
        throw new ReservaInvalidException(
                "Solo podés cancelar hasta el día anterior al retiro.");
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
            throw new ReservaInvalidException(
                    "Esta reserva ya no está pendiente, así que no se puede modificar.");
        }

        validarSolapamientoPorVehiculo(
            reserva.getPublicacion().getVehiculo().getIdVehiculo(),
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
            throw new ReservaInvalidException(
                    "Esta reserva ya no está pendiente, así que no se puede modificar.");
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

            throw new ReservaInvalidException(
                    "Faltan datos del carrito. Volvé a elegir las fechas.");
        }

        if (!carrito.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            throw new ReservaInvalidException(
                    "Tu carrito venció porque pasaron 15 minutos. Volvé a elegir el auto y las fechas.");
        }

        boolean tieneReservaPendiente =
                reservaRepository.existsByClienteIdUsuarioAndEstado(
                        carrito.getUsuario().getIdUsuario(),
                        EstadoReserva.PENDIENTE);

        if (tieneReservaPendiente) {
            throw new ReservaInvalidException(
                    "Ya tenés una reserva pendiente de pago. Pagala o cancelala desde Mis reservas antes de hacer otra.");
        }

        validarFechas(
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        validarSolapamientoPorVehiculo(
                carrito.getPublicacion().getVehiculo().getIdVehiculo(),
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
    @PreAuthorize("@seguridadDominio.esDuenioDeReserva(authentication, #idReserva) or hasRole('ADMIN')")
    public Optional<Reserva> getReservaById(Long idReserva) {
        return reservaRepository.findById(idReserva);
    }

    @PreAuthorize("@seguridadDominio.esMismoUsuario(authentication, #idUsuario)")
    @Override
    public Page<Reserva> getReservasByUsuario(Long idUsuario, PageRequest pageRequest) {
        
        return reservaRepository.findByClienteIdUsuario(idUsuario, pageRequest);
    }

    @PreAuthorize("isAuthenticated() and #emailUsuario == authentication.name")
    @Override
    public Page<Reserva> getMisReservas(String emailUsuario, PageRequest pageRequest) {
        return reservaRepository.findByClienteEmail(emailUsuario, pageRequest);
    }

    @PreAuthorize("isAuthenticated() and #emailPropietario == authentication.name")
    @Override
    public Page<Reserva> getReservasDeMisVehiculos(String emailPropietario, PageRequest pageRequest) {
        return reservaRepository.findByPublicacionVehiculoPropietarioEmail(emailPropietario, pageRequest);
    }

    @PreAuthorize("@seguridadDominio.esPropietarioDeReserva(authentication, #idReserva)")
    @Transactional
    @Override
    public Reserva finalizarReserva(Long idReserva) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(ReservaNotFoundException::new);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se puede finalizar una reserva confirmada.");
        }
        if (LocalDate.now().isBefore(reserva.getFechaFin())) {
            throw new IllegalStateException(
                    "Todavía no llegó la fecha de devolución, así que no podés finalizar la reserva.");
        }

        reserva.setEstado(EstadoReserva.FINALIZADA);
        reserva.setFechaFinalizacion(LocalDateTime.now());
        return reservaRepository.save(reserva);
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin)throws ReservaInvalidException {

        ValidacionVolanta.validarFechasRetiroYDevolucion(fechaInicio, fechaFin);

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new ReservaInvalidException(
                    "La devolución tiene que ser al menos un día después del retiro.");
        }
    }

    @Override
    public void validarSolapamientoPorVehiculo(
            Long idVehiculo,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws ReservaInvalidException {

        validarSolapamientoPorVehiculo(
                idVehiculo,
                fechaInicio,
                fechaFin,
                null);
    }

    private void validarSolapamientoPorVehiculo(
            Long idVehiculo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idReservaIgnorada)
            throws ReservaInvalidException {

        List<Reserva> reservasBloqueantes =
                reservaRepository
                        .findByPublicacion_Vehiculo_IdVehiculoAndEstadoIn(
                                idVehiculo,
                                List.of(
                                        EstadoReserva.PENDIENTE,
                                        EstadoReserva.CONFIRMADA));

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
                throw new ReservaInvalidException(
                        "El auto ya está reservado en esas fechas. Elegí otras.");
            }
        }
    }
    
}
