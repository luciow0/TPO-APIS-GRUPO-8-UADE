package com.uade.tpo.marketplace.controller;

import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.dto.ReservaResponse;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.service.ReservaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;


    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaResponse> getReservaById(@PathVariable Long idReserva) {

        Optional<Reserva> result = reservaService.getReservaById(idReserva);

        if (result.isPresent()) {
            return ResponseEntity.ok(convertirAResponse(result.get()));
        }

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Page<ReservaResponse>> getReservasByUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        PageRequest pageRequest;

        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        Page<ReservaResponse> result =
                reservaService
                        .getReservasByUsuario(idUsuario, pageRequest)
                        .map(this::convertirAResponse);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{idReserva}/cancelar")
    public ResponseEntity<ReservaResponse> cancelarReserva(
            @PathVariable Long idReserva,
            @RequestParam Long idUsuario)
            throws ReservaNotFoundException, ReservaInvalidException {

        Reserva result =
                reservaService.cancelarReserva(idReserva, idUsuario);

        return ResponseEntity.ok(convertirAResponse(result));
    }

    private ReservaResponse convertirAResponse(Reserva reserva) {

        long cantidadDias = ChronoUnit.DAYS.between(
                reserva.getFechaInicio(),
                reserva.getFechaFin());

        BigDecimal precioTotal =
                reserva.getPrecioDiaAplicado()
                        .multiply(BigDecimal.valueOf(cantidadDias))
                        .setScale(2, RoundingMode.HALF_UP);

        return new ReservaResponse(
                reserva.getIdReserva(),
                reserva.getPublicacion().getIdPublicacion(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getFechaCreacion(),
                reserva.getEstado(),
                reserva.getPrecioDiaAplicado(),
                cantidadDias,
                precioTotal);
    }
}
