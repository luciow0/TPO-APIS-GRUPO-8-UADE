package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exceptions.ReservaInvalidException;
import com.uade.tpo.marketplace.exceptions.ReservaNotFoundException;
import com.uade.tpo.marketplace.repository.ReservaRepository;
import com.uade.tpo.marketplace.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

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
            reserva.getFechaFin()
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
    public Reserva crearReserva(Long idUsuario, Long idPublicacion, LocalDate fechaInicio, LocalDate fechaFin) {
        // TODO: buscar Usuario cuando exista UsuarioRepository
        // TODO: buscar Publicacion cuando exista PublicacionRepository
        // TODO: validar que la Publicacion esté activa
        // TODO: validar que las fechas estén dentro de una Disponibilidad
        // TODO: crear la Reserva con estado PENDIENTE
        // TODO: guardar con reservaRepository.save()

        throw new UnsupportedOperationException("Crear reserva todavía no implementado");
    }

    @Override
    public Optional<Reserva> getReservaById(Long idReserva) {
        return reservaRepository.findById(idReserva);
    }

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

    private void validarSolapamiento(Long idPublicacion,LocalDate fechaInicio,LocalDate fechaFin) throws ReservaInvalidException {

        List<Reserva> reservasConfirmadas =
            reservaRepository.findByPublicacionIdPublicacionAndEstado(
                    idPublicacion,
                    EstadoReserva.CONFIRMADA
            );

        LocalDate finBloqueadoNuevaReserva = fechaFin.plusDays(1);

        for (Reserva reservaExistente : reservasConfirmadas) {

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
