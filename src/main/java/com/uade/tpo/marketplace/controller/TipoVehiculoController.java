package com.uade.tpo.marketplace.controller;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.service.TipoVehiculoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tipo-vehiculo")
public class TipoVehiculoController {

    @Autowired
private TipoVehiculoService tipoVehiculoService;

    @GetMapping
    public ResponseEntity<Page<TipoVehiculo>> getTipos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(tipoVehiculoService.getTipoVehiculo(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(tipoVehiculoService.getTipoVehiculo(PageRequest.of(page, size)));
    }

}
