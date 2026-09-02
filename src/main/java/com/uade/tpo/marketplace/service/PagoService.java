package com.uade.tpo.marketplace.service;

import java.util.Optional;
import com.uade.tpo.marketplace.exceptions.PagoDuplicateException;
import com.uade.tpo.marketplace.exceptions.PagoInvalidException;
import com.uade.tpo.marketplace.exceptions.PagoNotFoundException;
import com.uade.tpo.marketplace.exceptions.ReservaInvalidException;
import com.uade.tpo.marketplace.exceptions.ReservaNotFoundException;

import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.entity.Pago;

public interface PagoService {
    Optional<Pago> getPagoById(Long idPago);

    Optional<Pago> getPagoByReserva(Long idReserva);

    Pago crearPago(Long idReserva, MetodoPago metodoPago)
            throws ReservaNotFoundException, PagoDuplicateException;

    Pago aprobarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException;

    Pago rechazarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException;
}
