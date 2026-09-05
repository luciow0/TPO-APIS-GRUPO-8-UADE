package com.uade.tpo.marketplace.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.exception.PublicacionDuplicateException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
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
                .created(
                        URI.create(
                                "/publicaciones/"
                                        + publicacion.getIdPublicacion()))
                .body(publicacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publicacion> modificarPublicacion(
            @PathVariable Long id,
            @RequestBody PublicacionRequest request)
            throws PublicacionNotFoundException,
            PublicacionDuplicateException {

        Publicacion publicacion =
                publicacionService.modificarPublicacion(
                        id,
                        request);

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
    public ResponseEntity<List<Publicacion>>
            obtenerPublicacionesPorEstado(
                    @RequestParam EstadoPublicacion estado) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorEstado(estado));
    }

    @GetMapping("/filtros/precio")
    public ResponseEntity<List<Publicacion>>
            obtenerPublicacionesPorPrecio(
                    @RequestParam BigDecimal precioMin,
                    @RequestParam BigDecimal precioMax) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorPrecio(
                                precioMin,
                                precioMax));
    }

    @GetMapping("/filtros/tipo")
    public ResponseEntity<List<Publicacion>>
            obtenerPublicacionesPorTipoVehiculo(
                    @RequestParam Long idTipoVehiculo) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorTipoVehiculo(
                                idTipoVehiculo));
    }

    @GetMapping("/filtros/marca")
    public ResponseEntity<List<Publicacion>>
            obtenerPublicacionesPorMarca(
                    @RequestParam String marca) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorMarca(marca));
    }

    @GetMapping("/filtros/modelo")
    public ResponseEntity<List<Publicacion>>
            obtenerPublicacionesPorModelo(
                    @RequestParam String modelo) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorModelo(modelo));
    }

    @GetMapping("/filtros/provincia")
    public ResponseEntity<Page<Publicacion>>
            obtenerPublicacionesPorProvincia(
                    @RequestParam String provincia,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorProvincia(
                                provincia,
                                pageable));
    }

    @GetMapping("/filtros/provincia-ciudad")
    public ResponseEntity<Page<Publicacion>>
            obtenerPublicacionesPorProvinciaYCiudad(
                    @RequestParam String provincia,
                    @RequestParam String ciudad,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorProvinciaYCiudad(
                                provincia,
                                ciudad,
                                pageable));
    }

    @GetMapping("/filtros/provincia-ciudad-localidad")
    public ResponseEntity<Page<Publicacion>>
            obtenerPublicacionesPorProvinciaCiudadYLocalidad(
                    @RequestParam String provincia,
                    @RequestParam String ciudad,
                    @RequestParam String localidad,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorProvinciaCiudadYLocalidad(
                                provincia,
                                ciudad,
                                localidad,
                                pageable));
    }

    @GetMapping("/filtros/zona")
    public ResponseEntity<Page<Publicacion>> obtenerPublicacionesPorZona(
            @RequestParam String zona, Pageable pageable) {
        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorZona(zona, pageable));
    }

}
