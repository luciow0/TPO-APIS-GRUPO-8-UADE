package com.uade.tpo.marketplace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.Enum.RolResena;
import com.uade.tpo.marketplace.dto.ResenaPendienteResponse;
import com.uade.tpo.marketplace.dto.ResenaPublicaResponse;
import com.uade.tpo.marketplace.dto.ResenaRequest;
import com.uade.tpo.marketplace.dto.ResenaResponse;
import com.uade.tpo.marketplace.dto.ResenasDeReservaResponse;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.service.ResenaService;

@RestController
@RequestMapping("/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @PostMapping
    public ResponseEntity<ResenaResponse> crear(
            Authentication authentication,
            @RequestBody ResenaRequest request) throws ReservaNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resenaService.crear(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponse> editar(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody ResenaRequest request) {
        return ResponseEntity.ok(resenaService.editar(authentication.getName(), id, request));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<ResenaPendienteResponse>> pendientes(Authentication authentication) {
        return ResponseEntity.ok(resenaService.pendientes(authentication.getName()));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<ResenaResponse>> mias(Authentication authentication) {
        return ResponseEntity.ok(resenaService.mias(authentication.getName()));
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<ResenasDeReservaResponse> deReserva(
            Authentication authentication,
            @PathVariable Long idReserva) throws ReservaNotFoundException {
        return ResponseEntity.ok(resenaService.deReserva(authentication.getName(), idReserva));
    }

    // Público: solo reseñas ya publicadas.
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ResenaPublicaResponse>> deUsuario(
            @PathVariable Long idUsuario,
            @RequestParam RolResena rol) {
        return ResponseEntity.ok(resenaService.publicadasDeUsuario(idUsuario, rol));
    }
}
