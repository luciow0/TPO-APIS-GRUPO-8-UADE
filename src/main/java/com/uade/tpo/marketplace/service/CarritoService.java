package com.uade.tpo.marketplace.service;

import java.util.Optional;

import com.uade.tpo.marketplace.dto.AgregarCarritoRequest;
import com.uade.tpo.marketplace.dto.CarritoResponse;
import com.uade.tpo.marketplace.dto.ConfirmacionCarritoResponse;
import com.uade.tpo.marketplace.dto.ModificarFechasCarritoRequest;
import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.exception.CarritoDuplicateException;
import com.uade.tpo.marketplace.exception.CarritoInvalidException;
import com.uade.tpo.marketplace.exception.CarritoNotFoundException;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;

public interface CarritoService {

    CarritoResponse agregarPublicacionAlCarrito(
            Long idPublicacion,
            String emailUsuario,
            AgregarCarritoRequest request)
            throws CarritoInvalidException,
            CarritoDuplicateException,
            PublicacionNotFoundException,
            UsuarioNotFoundException;

    Optional<CarritoResponse> obtenerMiCarrito(String emailUsuario)
            throws UsuarioNotFoundException;

    CarritoResponse modificarFechas(
            Long idCarrito,
            ModificarFechasCarritoRequest request)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException;

    void eliminarCarrito(Long idCarrito)
            throws CarritoNotFoundException;

    ConfirmacionCarritoResponse confirmarCarrito(
            Long idCarrito,
            MetodoPago metodoPago)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException,
            ReservaInvalidException,
            ReservaNotFoundException,
            PagoDuplicateException,
            PagoInvalidException;
}
