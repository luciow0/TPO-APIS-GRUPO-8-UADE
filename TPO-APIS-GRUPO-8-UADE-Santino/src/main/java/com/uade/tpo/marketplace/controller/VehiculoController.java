package com.uade.tpo.marketplace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.service.VehiculoService;

@RestController
@RequestMapping("vehiculo")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<Page<Vehiculo>> getVehiculos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null) {
            return ResponseEntity.ok(vehiculoService.listar(PageRequest.of(0, Integer.MAX_VALUE)));
        }
        return ResponseEntity.ok(vehiculoService.listar(PageRequest.of(page, size)));
    }
}
