package com.uade.tpo.marketplace.controller;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.service.TipoVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("tipo-vehiculo")
public class TipoVehiculoController {

    @Autowired
    private TipoVehiculoService tipoVehiculoService;

    @GetMapping
    public ResponseEntity<List<TipoVehiculo>> getAllTipos() {
        return ResponseEntity.ok(tipoVehiculoService.getTiposVehiculo());
    }
}