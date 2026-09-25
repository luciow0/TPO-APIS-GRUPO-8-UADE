package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.dto.CheckoutResponse;
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exception.MercadoPagoException;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PagoNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.repository.PagoRepository;

@Service
public class PagoServiceImpl implements PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoServiceImpl.class);

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN')")
    public Pago aprobarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {
        Optional<Pago> pagoOptional = pagoRepository.findById(idPago);

        if (pagoOptional.isEmpty()) {
            throw new PagoNotFoundException();
        }

        Pago pago = pagoOptional.get();

        if (pago.getEstado() != EstadoPago.PENDIENTE
                || pago.getMetodo() == MetodoPago.MERCADO_PAGO) {
            throw new PagoInvalidException();
        }

        aplicarAprobacion(pago);

        return pagoRepository.save(pago);
    }

    @Override
    @PreAuthorize("@seguridadDominio.esDuenioDeReserva(authentication, #idReserva) or hasRole('ADMIN')")
    public Pago crearPago(Long idReserva, MetodoPago metodoPago)
            throws ReservaNotFoundException,
            PagoDuplicateException,
            PagoInvalidException {

        if (idReserva == null || metodoPago == null) {
            throw new PagoInvalidException();
        }

        Optional<Reserva> reservaOptional =
                reservaService.getReservaById(idReserva);

        if (reservaOptional.isEmpty()) {
            throw new ReservaNotFoundException();
        }

        Reserva reserva = reservaOptional.get();

        Optional<Pago> pagoExistente =
            pagoRepository.findByReservaIdReserva(idReserva);

        if (pagoExistente.isPresent()) {
            throw new PagoDuplicateException();
        }

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new PagoInvalidException();
        }

        BigDecimal monto = calcularMonto(reserva);

        Pago pago = new Pago();

        pago.setReserva(reserva);
        pago.setFecha(LocalDate.now());
        pago.setMonto(monto);
        pago.setMetodo(metodoPago);
        pago.setEstado(EstadoPago.PENDIENTE);

        return pagoRepository.save(pago);
    }

    @Override
    @PreAuthorize("@seguridadDominio.esDuenioDePago(authentication, #idPago) or hasRole('ADMIN')")
    public Optional<Pago> getPagoById(Long idPago) {
        return pagoRepository.findById(idPago);
    }

    @Override
    @PreAuthorize("@seguridadDominio.esDuenioDeReserva(authentication, #idReserva) or hasRole('ADMIN')")
    public Optional<Pago> getPagoByReserva(Long idReserva) {
        return pagoRepository.findByReservaIdReserva(idReserva);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN')")
    public Pago rechazarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {
        Optional<Pago> pagoOptional = pagoRepository.findById(idPago);

        if (pagoOptional.isEmpty()) {
            throw new PagoNotFoundException();
        }

        Pago pago = pagoOptional.get();

        if (pago.getEstado() != EstadoPago.PENDIENTE
                || pago.getMetodo() == MetodoPago.MERCADO_PAGO) {
            throw new PagoInvalidException();
        }

        aplicarRechazo(pago);

        return pagoRepository.save(pago);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("@seguridadDominio.esDuenioDePago(authentication, #idPago) or hasRole('ADMIN')")
    public CheckoutResponse crearCheckout(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            MercadoPagoException {

        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(PagoNotFoundException::new);

        if (pago.getMetodo() != MetodoPago.MERCADO_PAGO
                || pago.getEstado() != EstadoPago.PENDIENTE
                || !MercadoPagoService.vencimientoReserva(pago).isAfter(LocalDateTime.now())) {
            throw new PagoInvalidException();
        }

        Preference preferencia;

        if (pago.getPreferenceId() != null) {
            preferencia = mercadoPagoService.obtenerPreferencia(pago.getPreferenceId());
        } else {
            preferencia = mercadoPagoService.crearPreferencia(pago);
            pago.setPreferenceId(preferencia.getId());
            pagoRepository.save(pago);
        }

        return new CheckoutResponse(
                pago.getIdPago(),
                preferencia.getId(),
                preferencia.getInitPoint());
    }

    // Sin @PreAuthorize: lo invocan el webhook y la redireccion de Mercado Pago, no un usuario logueado.
    // El estado se toma de la API de Mercado Pago, nunca de lo que manda el navegador.
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Pago> procesarPagoMercadoPago(Long paymentId)
            throws MercadoPagoException,
            ReservaNotFoundException, ReservaInvalidException {

        Payment pagoMercadoPago = mercadoPagoService.obtenerPago(paymentId);

        Long idPago = parsearId(pagoMercadoPago.getExternalReference());

        if (idPago == null) {
            return Optional.empty();
        }

        Optional<Pago> pagoOptional = buscarPagoMercadoPago(idPago);

        if (pagoOptional.isEmpty()) {
            return Optional.empty();
        }

        Pago pago = pagoOptional.get();

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            return Optional.of(pago);
        }

        pago.setMercadoPagoPaymentId(String.valueOf(paymentId));

        switch (String.valueOf(pagoMercadoPago.getStatus())) {
            case "approved" -> aplicarAprobacion(pago);
            case "rejected", "cancelled" -> aplicarRechazo(pago);
            default -> {
                // pending / in_process: el pago sigue PENDIENTE.
            }
        }

        return Optional.of(pagoRepository.save(pago));
    }

    // Libera las fechas del auto y al usuario si no pago con Mercado Pago a tiempo.
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void vencerReservasSinPagar() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(MercadoPagoService.MINUTOS_PARA_PAGAR);

        List<Pago> vencidos = pagoRepository.findByMetodoAndEstadoAndReservaFechaCreacionBefore(
                MetodoPago.MERCADO_PAGO, EstadoPago.PENDIENTE, limite);

        for (Pago pago : vencidos) {
            try {
                aplicarRechazo(pago);
                pagoRepository.save(pago);
            } catch (ReservaNotFoundException | ReservaInvalidException e) {
                log.warn("No se pudo vencer la reserva del pago {}", pago.getIdPago(), e);
            }
        }
    }

    @Override
    public Optional<Pago> buscarPagoMercadoPago(Long idPago) {
        return pagoRepository.findById(idPago)
                .filter(pago -> pago.getMetodo() == MetodoPago.MERCADO_PAGO);
    }

    private void aplicarAprobacion(Pago pago)
            throws ReservaNotFoundException, ReservaInvalidException {

        reservaService.confirmarReserva(pago.getReserva().getIdReserva());
        pago.setEstado(EstadoPago.APROBADO);
    }

    private void aplicarRechazo(Pago pago)
            throws ReservaNotFoundException, ReservaInvalidException {

        reservaService.rechazarReserva(pago.getReserva().getIdReserva());
        pago.setEstado(EstadoPago.RECHAZADO);
    }

    private Long parsearId(String valor) {
        try {
            return valor == null ? null : Long.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal calcularMonto(Reserva reserva)
            throws PagoInvalidException {

        if (reserva.getFechaInicio() == null
                || reserva.getFechaFin() == null
                || reserva.getPrecioDiaAplicado() == null) {

            throw new PagoInvalidException();
        }

        long cantidadDias = ChronoUnit.DAYS.between(
                reserva.getFechaInicio(),
                reserva.getFechaFin());

        if (cantidadDias <= 0) {
            throw new PagoInvalidException();
        }

        return reserva.getPrecioDiaAplicado()
                .multiply(BigDecimal.valueOf(cantidadDias));
    }

}
