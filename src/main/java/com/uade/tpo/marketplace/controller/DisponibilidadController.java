package com.uade.tpo.marketplace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.DisponibilidadRequest;
import com.uade.tpo.marketplace.dto.DisponibilidadResponse;
import com.uade.tpo.marketplace.entity.Disponibilidad;
import com.uade.tpo.marketplace.exception.DisponibilidadNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.service.DisponibilidadService;

@RestController
@RequestMapping("disponibilidades")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadService disponibilidadService;

    @GetMapping
    public ResponseEntity<Page<DisponibilidadResponse>> obtenerDisponibilidades(Pageable pageable) {

        return ResponseEntity.ok(
                disponibilidadService.obtenerDisponibilidades(pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> obtenerDisponibilidadPorId(
            @PathVariable Long id)
            throws DisponibilidadNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadService.obtenerDisponibilidadPorId(id);

        return ResponseEntity.ok(convertirAResponse(disponibilidad));
    }

    @GetMapping("/publicacion/{idPublicacion}")
    @PreAuthorize("@seguridadDominio.puedeVerPublicacion(authentication, #idPublicacion)")
    public ResponseEntity<List<DisponibilidadResponse>>
            obtenerDisponibilidadesPorPublicacion(
                    @PathVariable Long idPublicacion)
                    throws PublicacionNotFoundException {

        return ResponseEntity.ok(
                disponibilidadService
                        .obtenerDisponibilidadesPorPublicacion(
                                idPublicacion)
                        .stream()
                        .map(this::convertirAResponse)
                        .toList());
    }

    @PostMapping
    public ResponseEntity<DisponibilidadResponse> crearDisponibilidad(
            @RequestBody DisponibilidadRequest request)
            throws PublicacionNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadService.crearDisponibilidad(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/disponibilidades/"
                                        + disponibilidad.getIdDisponibilidad()))
                .body(convertirAResponse(disponibilidad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> modificarDisponibilidad(
            @PathVariable Long id,
            @RequestBody DisponibilidadRequest request)
            throws DisponibilidadNotFoundException,
            PublicacionNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadService.modificarDisponibilidad(
                        id,
                        request);

        return ResponseEntity.ok(convertirAResponse(disponibilidad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDisponibilidad(
            @PathVariable Long id)
            throws DisponibilidadNotFoundException {

        disponibilidadService.eliminarDisponibilidad(id);

        return ResponseEntity.noContent().build();
    }

    private DisponibilidadResponse convertirAResponse(Disponibilidad disponibilidad) {
        return new DisponibilidadResponse(
                disponibilidad.getIdDisponibilidad(),
                disponibilidad.getFechaInicio(),
                disponibilidad.getFechaFin(),
                disponibilidad.getPublicacion().getIdPublicacion());
    }
}
