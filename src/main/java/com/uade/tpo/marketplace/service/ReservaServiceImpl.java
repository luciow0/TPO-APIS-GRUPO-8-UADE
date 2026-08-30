package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.repository.ReservaRepository;
import com.uade.tpo.marketplace.service.ReservaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    public Reserva crearReserva(
            Long idUsuario,
            Long idPublicacion,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        return null;
    }

    @Override
    public Reserva getReservaById(Long idReserva) {
        return null;
    }

    @Override
    public Page<Reserva> getReservasByUsuario(
            Long idUsuario,
            PageRequest pageRequest
    ) {
        return null;
    }

    @Override
    public Reserva cancelarReserva(
            Long idReserva,
            Long idUsuario
    ) {
        return null;
    }

    @Override
    public Reserva confirmarReserva(Long idReserva) {
        return null;
    }
}