package com.uade.tpo.marketplace.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import com.uade.tpo.marketplace.entity.ImagenVehiculo;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.service.ImagenVehiculoService;
import com.uade.tpo.marketplace.service.VehiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("imagen-vehiculo")
public class ImagenVehiculoController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private final ImagenVehiculoService imagenVehiculoService;
    private final VehiculoService vehiculoService;

    public ImagenVehiculoController(ImagenVehiculoService imagenVehiculoService, VehiculoService vehiculoService) {
        this.imagenVehiculoService = imagenVehiculoService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public ResponseEntity<Page<ImagenVehiculo>> listar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int pageNum = page != null ? page : DEFAULT_PAGE;
        int pageSize = size != null ? size : DEFAULT_SIZE;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return ResponseEntity.ok(imagenVehiculoService.listar(pageable));
    }

    @GetMapping("/por-vehiculo/{idVehiculo}")
    public ResponseEntity<List<ImagenVehiculo>> listarPorVehiculo(@PathVariable Long idVehiculo) {
        return ResponseEntity.ok(imagenVehiculoService.buscarPorVehiculo(idVehiculo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagenVehiculo> obtener(@PathVariable Long id) {
        return imagenVehiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Imagen de vehiculo no encontrada con id: " + id));
    }

    @PostMapping
    public ResponseEntity<ImagenVehiculo> crear(@Valid @RequestBody ImagenVehiculo imagenVehiculo) {
        // Validar que el vehículo existe
        if (imagenVehiculo.getVehiculo() == null || imagenVehiculo.getVehiculo().getIdVehiculo() == null) {
            throw new IllegalArgumentException("El vehiculo es requerido");
        }
        vehiculoService.buscarPorId(imagenVehiculo.getVehiculo().getIdVehiculo())
                .orElseThrow(() -> new EntityNotFoundException("Vehiculo no encontrado con id: " + imagenVehiculo.getVehiculo().getIdVehiculo()));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenVehiculoService.guardar(imagenVehiculo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImagenVehiculo> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody ImagenVehiculo imagenVehiculo) {
        // Validar que la imagen existe
        imagenVehiculoService.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Imagen de vehiculo no encontrada con id: " + id));
        
        // Validar que el vehículo existe si se intenta cambiar
        if (imagenVehiculo.getVehiculo() != null && imagenVehiculo.getVehiculo().getIdVehiculo() != null) {
            vehiculoService.buscarPorId(imagenVehiculo.getVehiculo().getIdVehiculo())
                    .orElseThrow(() -> new EntityNotFoundException("Vehiculo no encontrado con id: " + imagenVehiculo.getVehiculo().getIdVehiculo()));
        }
        
        return ResponseEntity.ok(imagenVehiculoService.actualizar(id, imagenVehiculo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        imagenVehiculoService.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Imagen de vehiculo no encontrada con id: " + id));
        imagenVehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}