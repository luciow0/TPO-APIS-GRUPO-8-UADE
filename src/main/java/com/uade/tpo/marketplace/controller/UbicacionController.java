package com.uade.tpo.marketplace.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.marketplace.dto.UbicacionRequest;
import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.exception.UbicacionNotFoundException;
import com.uade.tpo.marketplace.service.UbicacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @PostMapping
    public ResponseEntity<Ubicacion> crear(@Valid @RequestBody UbicacionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionService.crear(req));
    }

    @GetMapping
    public ResponseEntity<List<Ubicacion>> listar() {
        return ResponseEntity.ok(ubicacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> buscarPorId(@PathVariable Long id)
            throws UbicacionNotFoundException {
        return ResponseEntity.ok(ubicacionService.obtenerUbicacionPorId(id)
                .orElseThrow(UbicacionNotFoundException::new));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizar(@PathVariable Long id,
                                                @Valid @RequestBody UbicacionRequest req) throws UbicacionNotFoundException {
        return ResponseEntity.ok(ubicacionService.updateUbicacion(id, req));
    }

}
