package com.uade.tpo.marketplace.service;

import java.util.Optional;
import com.uade.tpo.marketplace.exception.MercadoPagoException;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PagoNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;

import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.dto.CheckoutResponse;
import com.uade.tpo.marketplace.entity.Pago;

public interface PagoService {
    Optional<Pago> getPagoById(Long idPago);

    Optional<Pago> getPagoByReserva(Long idReserva);

    Pago crearPago(Long idReserva, MetodoPago metodoPago)
            throws ReservaNotFoundException,
            PagoDuplicateException,
            PagoInvalidException;

    Pago aprobarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException;

    Pago rechazarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException;

    CheckoutResponse crearCheckout(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            MercadoPagoException;

    Optional<Pago> procesarPagoMercadoPago(Long paymentId)
            throws MercadoPagoException,
            ReservaNotFoundException, ReservaInvalidException;

    Optional<Pago> buscarPagoMercadoPago(Long idPago);
}
