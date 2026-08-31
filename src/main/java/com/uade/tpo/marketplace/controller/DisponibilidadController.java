package com.uade.tpo.marketplace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.DisponibilidadRequest;
import com.uade.tpo.marketplace.entity.Disponibilidad;
import com.uade.tpo.marketplace.exceptions.DisponibilidadNotFoundException;
import com.uade.tpo.marketplace.exceptions.PublicacionNotFoundException;
import com.uade.tpo.marketplace.service.DisponibilidadService;

@RestController
@RequestMapping("disponibilidades")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadService disponibilidadService;

    @GetMapping
    public ResponseEntity<List<Disponibilidad>> obtenerDisponibilidades() {

        return ResponseEntity.ok(
                disponibilidadService.obtenerDisponibilidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Disponibilidad> obtenerDisponibilidadPorId(
            @PathVariable Long id)
            throws DisponibilidadNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadService.obtenerDisponibilidadPorId(id);

        return ResponseEntity.ok(disponibilidad);
    }

    @GetMapping("/publicacion/{idPublicacion}")
    public ResponseEntity<List<Disponibilidad>>
            obtenerDisponibilidadesPorPublicacion(
                    @PathVariable Long idPublicacion)
                    throws PublicacionNotFoundException {

        return ResponseEntity.ok(
                disponibilidadService
                        .obtenerDisponibilidadesPorPublicacion(
                                idPublicacion));
    }

    @PostMapping
    public ResponseEntity<Disponibilidad> crearDisponibilidad(
            @RequestBody DisponibilidadRequest request)
            throws PublicacionNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadService.crearDisponibilidad(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/disponibilidades/"
                                        + disponibilidad.getIdDisponibilidad()))
                .body(disponibilidad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Disponibilidad> modificarDisponibilidad(
            @PathVariable Long id,
            @RequestBody DisponibilidadRequest request)
            throws DisponibilidadNotFoundException,
            PublicacionNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadService.modificarDisponibilidad(
                        id,
                        request);

        return ResponseEntity.ok(disponibilidad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDisponibilidad(
            @PathVariable Long id)
            throws DisponibilidadNotFoundException {

        disponibilidadService.eliminarDisponibilidad(id);

        return ResponseEntity.noContent().build();
    }
}