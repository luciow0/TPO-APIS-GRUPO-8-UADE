package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.util.Optional;

import com.uade.tpo.marketplace.dto.CarritoRequest;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.exceptions.CarritoDuplicateException;
import com.uade.tpo.marketplace.exceptions.CarritoInvalidException;
import com.uade.tpo.marketplace.exceptions.CarritoNotFoundException;
import com.uade.tpo.marketplace.exceptions.PublicacionNotFoundException;

public interface CarritoService {

    Carrito crearCarrito(CarritoRequest request)
            throws CarritoInvalidException,
            CarritoDuplicateException,
            PublicacionNotFoundException;

    Optional<Carrito> obtenerCarritoPorUsuario(Long idUsuario);

    Carrito modificarFechas(
            Long idCarrito,
            Long idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws CarritoNotFoundException,
            CarritoInvalidException;

    void eliminarCarrito(Long idCarrito, Long idUsuario)
            throws CarritoNotFoundException,
            CarritoInvalidException;
}
