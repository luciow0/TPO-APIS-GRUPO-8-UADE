package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

public interface ReservaService {

    Reserva crearReserva(
            Long idUsuario,
            Long idPublicacion,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    Reserva getReservaById(Long idReserva);

    Page<Reserva> getReservasByUsuario(
            Long idUsuario,
            PageRequest pageRequest
    );

    Reserva cancelarReserva(
            Long idReserva,
            Long idUsuario
    );

    Reserva confirmarReserva(Long idReserva);
}