package com.uade.tpo.marketplace.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.marketplace.dto.UbicacionRequest;
import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.exception.UbicacionNotFoundException;
import com.uade.tpo.marketplace.service.UbicacionService;
import com.uade.tpo.marketplace.service.ValidacionVolanta;

@RestController
@RequestMapping("/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @PostMapping
    public ResponseEntity<Ubicacion> crear(@RequestBody UbicacionRequest req) {
        validarUbicacion(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionService.crear(req));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ubicacion>> listar() {
        return ResponseEntity.ok(ubicacionService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@seguridadDominio.puedeVerUbicacion(authentication, #id)")
    public ResponseEntity<Ubicacion> buscarPorId(@PathVariable Long id)
            throws UbicacionNotFoundException {
        return ResponseEntity.ok(ubicacionService.obtenerUbicacionPorId(id)
                .orElseThrow(UbicacionNotFoundException::new));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ubicacion> actualizar(@PathVariable Long id,
                                                @RequestBody UbicacionRequest req) throws UbicacionNotFoundException {
        validarUbicacion(req);
        return ResponseEntity.ok(ubicacionService.updateUbicacion(id, req));
    }

    private void validarUbicacion(UbicacionRequest req) {
        ValidacionVolanta.validarDireccionNoVacia(req.getDireccion());
        ValidacionVolanta.validarCiudadNoVacia(req.getCiudad());
        ValidacionVolanta.validarProvinciaNoVacia(req.getProvincia());
        ValidacionVolanta.validarLocalidadNoVacia(req.getLocalidad());
        ValidacionVolanta.validarCodigoPostalNoVacio(req.getCodigoPostal());
        ValidacionVolanta.validarZonaNoVacia(req.getZona());
        ValidacionVolanta.validarCoordenadas(req.getLatitud(), req.getLongitud());
    }

}
