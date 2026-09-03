package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.uade.tpo.marketplace.exceptions.ReservaInvalidException;
import com.uade.tpo.marketplace.exceptions.ReservaNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservaService {

        Optional<Reserva> getReservaById(Long idReserva);

        Reserva crearReservaDesdeCarrito(Carrito carrito)
                throws ReservaInvalidException;

        Page<Reserva> getReservasByUsuario(Long idUsuario,PageRequest pageRequest);

        Reserva cancelarReserva(Long idReserva,Long idUsuario )throws ReservaNotFoundException, ReservaInvalidException;

        Reserva confirmarReserva(Long idReserva)throws ReservaNotFoundException, ReservaInvalidException;

        Reserva rechazarReserva(Long idReserva)throws ReservaNotFoundException, ReservaInvalidException;

        void validarSolapamiento(
                Long idPublicacion,
                LocalDate fechaInicio,
                LocalDate fechaFin)
                throws ReservaInvalidException;
}
