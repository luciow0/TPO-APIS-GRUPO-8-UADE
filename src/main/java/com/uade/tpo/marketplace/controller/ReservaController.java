package com.uade.tpo.marketplace.controller;

import com.uade.tpo.marketplace.dto.ReservaRequest;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exceptions.ReservaInvalidException;
import com.uade.tpo.marketplace.exceptions.ReservaNotFoundException;
import com.uade.tpo.marketplace.service.ReservaService;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;


    @GetMapping("/{idReserva}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Long idReserva) {

        Optional<Reserva> result = reservaService.getReservaById(idReserva);

        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Page<Reserva>> getReservasByUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        PageRequest pageRequest;

        if (page == null || size == null) {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        Page<Reserva> result =
                reservaService.getReservasByUsuario(idUsuario, pageRequest);

        return ResponseEntity.ok(result);
    }


    @PostMapping
    public ResponseEntity<Object> crearReserva(
            @RequestBody ReservaRequest reservaRequest)
            throws ReservaInvalidException {

        Reserva result = reservaService.crearReserva(
                reservaRequest.getIdUsuario(),
                reservaRequest.getIdPublicacion(),
                reservaRequest.getFechaInicio(),
                reservaRequest.getFechaFin()
        );

        return ResponseEntity
                .created(URI.create("/reservas/" + result.getIdReserva()))
                .body(result);
    }


    @PutMapping("/{idReserva}/cancelar")
    public ResponseEntity<Reserva> cancelarReserva(
            @PathVariable Long idReserva,
            @RequestParam Long idUsuario)
            throws ReservaNotFoundException, ReservaInvalidException {

        Reserva result =
                reservaService.cancelarReserva(idReserva, idUsuario);

        return ResponseEntity.ok(result);
    }
}