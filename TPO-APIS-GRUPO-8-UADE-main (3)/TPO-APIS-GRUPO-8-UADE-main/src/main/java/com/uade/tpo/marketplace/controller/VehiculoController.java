package com.uade.tpo.marketplace.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.dto.VehiculoDTO;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.service.VehiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("vehiculo")
public class VehiculoController {
    
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    
    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<Page<Vehiculo>> getVehiculos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int pageNum = page != null ? page : DEFAULT_PAGE;
        int pageSize = size != null ? size : DEFAULT_SIZE;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return ResponseEntity.ok(vehiculoService.listar(pageable));
    }

    @PostMapping
    public ResponseEntity<Vehiculo> crearVehiculo(@Valid @RequestBody VehiculoDTO vehiculoDTO) {
        // Validar que la patente no exista
        if (vehiculoService.existePatente(vehiculoDTO.getPatente())) {
            throw new IllegalArgumentException("La patente " + vehiculoDTO.getPatente() + " ya existe");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.guardar(vehiculoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizarVehiculo(@PathVariable Long id, @Valid @RequestBody VehiculoDTO vehiculoDTO) {
        return ResponseEntity.ok(vehiculoService.actualizar(id, vehiculoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {
        vehiculoService.buscarPorId(id)
            .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con id: " + id));
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtenerVehiculo(@PathVariable Long id) {
        return vehiculoService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con id: " + id));
    }

    @GetMapping("/patente/{patente}")
    public ResponseEntity<Vehiculo> buscarPorPatente(@PathVariable String patente) {
        return vehiculoService.buscarPorPatente(patente)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new EntityNotFoundException("Vehículo con patente " + patente + " no encontrado"));
    }
}