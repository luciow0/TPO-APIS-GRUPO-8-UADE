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
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.service.PagoService;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/{idPago}")
    public ResponseEntity<Pago> getPagoById(@PathVariable Long idPago) {

        Optional<Pago> result = pagoService.getPagoById(idPago);

        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<Pago> getPagoByReserva(@PathVariable Long idReserva) {

        Optional<Pago> result = pagoService.getPagoByReserva(idReserva);

        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Object> crearPago(@RequestBody PagoRequest pagoRequest) {

        Pago result = pagoService.crearPago(pagoRequest.getIdReserva(),pagoRequest.getMetodoPago());

        return ResponseEntity.created(URI.create("/pagos/" + result.getIdPago())).body(result);
    }

    @PutMapping("/{idPago}/aprobar")
    public ResponseEntity<Pago> aprobarPago(@PathVariable Long idPago) {

        Pago result = pagoService.aprobarPago(idPago);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{idPago}/rechazar")
    public ResponseEntity<Pago> rechazarPago(@PathVariable Long idPago) {

        Pago result = pagoService.rechazarPago(idPago);

        return ResponseEntity.ok(result);
    }

}
