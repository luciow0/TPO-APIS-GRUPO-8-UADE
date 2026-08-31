package com.uade.tpo.marketplace.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.PublicacionRequest;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.enums.EstadoPublicacion;
import com.uade.tpo.marketplace.exceptions.PublicacionDuplicateException;
import com.uade.tpo.marketplace.exceptions.PublicacionNotFoundException;
import com.uade.tpo.marketplace.service.PublicacionService;

@RestController
@RequestMapping("publicaciones")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @GetMapping
    public ResponseEntity<List<Publicacion>> obtenerPublicaciones() {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        Publicacion publicacion =
                publicacionService.obtenerPublicacionPorId(id);

        return ResponseEntity.ok(publicacion);
    }

    @PostMapping
    public ResponseEntity<Publicacion> crearPublicacion(
            @RequestBody PublicacionRequest request)
            throws PublicacionDuplicateException {

        Publicacion publicacion =
                publicacionService.crearPublicacion(request);

        return ResponseEntity
                .created(URI.create(
                        "/publicaciones/" + publicacion.getIdPublicacion()))
                .body(publicacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacion> modificarPublicacion(
            @PathVariable Long id,
            @RequestBody PublicacionRequest request)
            throws PublicacionNotFoundException,
            PublicacionDuplicateException {

        Publicacion publicacion =
                publicacionService.modificarPublicacion(id, request);

        return ResponseEntity.ok(publicacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        publicacionService.eliminarPublicacion(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/pausar")
    public ResponseEntity<Publicacion> pausarPublicacion(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        Publicacion publicacion =
                publicacionService.pausarPublicacion(id);

        return ResponseEntity.ok(publicacion);
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Publicacion> reactivarPublicacion(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        Publicacion publicacion =
                publicacionService.reactivarPublicacion(id);

        return ResponseEntity.ok(publicacion);
    }

    @GetMapping("/filtros/estado")
    public ResponseEntity<List<Publicacion>> obtenerPorEstado(
            @RequestParam EstadoPublicacion estado) {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorEstado(estado));
    }

    @GetMapping("/filtros/precio")
    public ResponseEntity<List<Publicacion>> obtenerPorPrecio(
            @RequestParam BigDecimal precioMin,
            @RequestParam BigDecimal precioMax) {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorPrecio(
                        precioMin,
                        precioMax));
    }

    @GetMapping("/filtros/tipo")
    public ResponseEntity<List<Publicacion>> obtenerPorTipoVehiculo(
            @RequestParam Long idTipoVehiculo) {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorTipoVehiculo(
                        idTipoVehiculo));
    }

    @GetMapping("/filtros/marca")
    public ResponseEntity<List<Publicacion>> obtenerPorMarca(
            @RequestParam String marca) {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorMarca(marca));
    }

    @GetMapping("/filtros/modelo")
    public ResponseEntity<List<Publicacion>> obtenerPorModelo(
            @RequestParam String modelo) {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorModelo(modelo));
    }

    @GetMapping("/filtros/zona")
    public ResponseEntity<List<Publicacion>> obtenerPorZona(
            @RequestParam String zona) {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorZona(zona));
    }
}