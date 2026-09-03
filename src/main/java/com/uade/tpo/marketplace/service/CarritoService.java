package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.util.Optional;

import com.uade.tpo.marketplace.dto.CarritoRequest;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exception.CarritoDuplicateException;
import com.uade.tpo.marketplace.exception.CarritoInvalidException;
import com.uade.tpo.marketplace.exception.CarritoNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;

public interface CarritoService {

    Carrito crearCarrito(CarritoRequest request)
            throws CarritoInvalidException,
            CarritoDuplicateException,
            PublicacionNotFoundException,
            UsuarioNotFoundException;

    Optional<Carrito> obtenerCarritoPorUsuario(Long idUsuario);

    Carrito modificarFechas(
            Long idCarrito,
            Long idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException;

    void eliminarCarrito(Long idCarrito, Long idUsuario)
            throws CarritoNotFoundException,
            CarritoInvalidException;

    Reserva continuarReserva(Long idCarrito, Long idUsuario)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException,
            ReservaInvalidException;
}
