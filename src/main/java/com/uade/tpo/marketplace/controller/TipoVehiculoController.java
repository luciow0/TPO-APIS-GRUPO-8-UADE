package com.uade.tpo.marketplace.controller;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.service.TipoVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    //TODO: Alta de un tipo de vehiculo. Pensado para el ADMIN (a proteger con rol cuando
    // este la seguridad): .requestMatchers(HttpMethod.POST, "/tipo-vehiculo").hasAuthority(Role.ADMIN.name())
    @PostMapping
    public ResponseEntity<TipoVehiculo> crearTipoVehiculo(@RequestBody TipoVehiculo tipoVehiculo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tipoVehiculoService.crearTipoVehiculo(tipoVehiculo));
    }
}
