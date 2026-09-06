package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PagoNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.repository.PagoRepository;

@Service
public class PagoServiceImpl implements PagoService {
    
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaService reservaService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("@seguridadDominio.esDuenioDePago(authentication, #idPago) or hasRole('ADMIN')")
    public Pago aprobarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {
        Optional<Pago> pagoOptional = pagoRepository.findById(idPago);

        if (pagoOptional.isEmpty()) {
            throw new PagoNotFoundException();
        }

        Pago pago = pagoOptional.get();

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new PagoInvalidException();
        }

        reservaService.confirmarReserva(
            pago.getReserva().getIdReserva()
        );

        pago.setEstado(EstadoPago.APROBADO);

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
    @PreAuthorize("@seguridadDominio.esDuenioDePago(authentication, #idPago) or hasRole('ADMIN')")
    public Pago rechazarPago(Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {
        Optional<Pago> pagoOptional = pagoRepository.findById(idPago);

        if (pagoOptional.isEmpty()) {
            throw new PagoNotFoundException();
        }

        Pago pago = pagoOptional.get();

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new PagoInvalidException();
        }

        reservaService.rechazarReserva(
            pago.getReserva().getIdReserva()
        );

        pago.setEstado(EstadoPago.RECHAZADO);

        return pagoRepository.save(pago);
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
