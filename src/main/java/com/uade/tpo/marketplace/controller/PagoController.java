package com.uade.tpo.marketplace.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.PagoRequest;
import com.uade.tpo.marketplace.dto.PagoResponse;
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PagoNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.service.PagoService;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/{idPago}")
    public ResponseEntity<PagoResponse> getPagoById(@PathVariable Long idPago) {

        Optional<Pago> result = pagoService.getPagoById(idPago);

        if (result.isPresent()) {
            return ResponseEntity.ok(convertirAResponse(result.get()));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<PagoResponse> getPagoByReserva(@PathVariable Long idReserva) {

        Optional<Pago> result = pagoService.getPagoByReserva(idReserva);

        if (result.isPresent()) {
            return ResponseEntity.ok(convertirAResponse(result.get()));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crearPago(@RequestBody PagoRequest pagoRequest)
            throws ReservaNotFoundException,
            PagoDuplicateException,
            PagoInvalidException {

        if (pagoRequest == null) {
            throw new PagoInvalidException();
        }

        Pago result = pagoService.crearPago(
                pagoRequest.getIdReserva(),
                pagoRequest.getMetodoPago());

        return ResponseEntity
                .created(URI.create("/pagos/" + result.getIdPago()))
                .body(convertirAResponse(result));
    }

    @PutMapping("/{idPago}/aprobar")
    public ResponseEntity<PagoResponse> aprobarPago(@PathVariable Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {

        Pago result = pagoService.aprobarPago(idPago);

        return ResponseEntity.ok(convertirAResponse(result));
    }

    @PutMapping("/{idPago}/rechazar")
    public ResponseEntity<PagoResponse> rechazarPago(@PathVariable Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {

        Pago result = pagoService.rechazarPago(idPago);

        return ResponseEntity.ok(convertirAResponse(result));
    }

    private PagoResponse convertirAResponse(Pago pago) {
        return new PagoResponse(
                pago.getIdPago(),
                pago.getReserva().getIdReserva(),
                pago.getFecha(),
                pago.getMonto(),
                pago.getEstado(),
                pago.getMetodo());
    }

}
