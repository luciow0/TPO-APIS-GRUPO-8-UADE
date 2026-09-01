package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exceptions.ReservaInvalidException;
import com.uade.tpo.marketplace.exceptions.ReservaNotFoundException;
import com.uade.tpo.marketplace.repository.PagoRepository;
import com.uade.tpo.marketplace.repository.ReservaRepository;
import com.uade.tpo.marketplace.service.ReservaService;

public class PagoServiceImpl implements PagoService {
    
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @Override
    public Pago aprobarPago(Long idPago)throws ReservaNotFoundException, ReservaInvalidException {
        Optional<Pago> pagoOptional = pagoRepository.findById(idPago);

        if (pagoOptional.isEmpty()) {
            throw new RuntimeException("Pago no encontrado");
        }

        Pago pago = pagoOptional.get();

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new RuntimeException("Solo se puede aprobar un pago pendiente");
        }

        reservaService.confirmarReserva(
            pago.getReserva().getIdReserva()
        );

        pago.setEstado(EstadoPago.APROBADO);

        return pagoRepository.save(pago);
    }

    @Override
    public Pago crearPago(Long idReserva, MetodoPago metodoPago) {
        Optional<Reserva> reservaOptional = reservaRepository.findById(idReserva);

        if (reservaOptional.isEmpty()) {
            throw new RuntimeException("Reserva no encontrada");
        }

        Reserva reserva = reservaOptional.get();

        Optional<Pago> pagoExistente =
            pagoRepository.findByReservaIdReserva(idReserva);

        if (pagoExistente.isPresent()) {
            throw new RuntimeException("La reserva ya tiene un pago asociado");
        }

        double monto = calcularMonto(reserva);

        Pago pago = new Pago();

        pago.setReserva(reserva);
        pago.setFecha(LocalDate.now());
        pago.setMonto((float) monto);
        pago.setMetodo(metodoPago);
        pago.setEstado(EstadoPago.PENDIENTE);

        return pagoRepository.save(pago);
    }

    @Override
    public Optional<Pago> getPagoById(Long idPago) {
        return pagoRepository.findById(idPago);
    }

    @Override
    public Optional<Pago> getPagoByReserva(Long idReserva) {
        return pagoRepository.findByReservaIdReserva(idReserva);

    }

    @Override
    public Pago rechazarPago(Long idPago) {
        Optional<Pago> pagoOptional = pagoRepository.findById(idPago);

        if (pagoOptional.isEmpty()) {
            throw new RuntimeException("Pago no encontrado");
        }

        Pago pago = pagoOptional.get();

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new RuntimeException("Solo se puede rechazar un pago pendiente");
        }

        pago.setEstado(EstadoPago.RECHAZADO);

        return pagoRepository.save(pago);
    }

    private double calcularMonto(Reserva reserva) {
        long cantidadDias = ChronoUnit.DAYS.between(reserva.getFechaInicio(),reserva.getFechaFin());

        return cantidadDias * reserva.getPrecioDiaAplicado();
    }

}
