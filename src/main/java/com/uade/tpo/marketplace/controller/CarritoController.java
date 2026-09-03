package com.uade.tpo.marketplace.controller;

import java.net.URI;
import java.util.Optional;

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

import com.uade.tpo.marketplace.dto.CarritoRequest;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.exception.CarritoDuplicateException;
import com.uade.tpo.marketplace.exception.CarritoInvalidException;
import com.uade.tpo.marketplace.exception.CarritoNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;
import com.uade.tpo.marketplace.service.CarritoService;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PostMapping
    public ResponseEntity<Carrito> crearCarrito(
            @RequestBody CarritoRequest request)
            throws CarritoInvalidException,
            CarritoDuplicateException,
            PublicacionNotFoundException,
            UsuarioNotFoundException {

        Carrito carrito = carritoService.crearCarrito(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/carritos/"
                                        + carrito.getIdCarrito()))
                .body(carrito);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Carrito> obtenerCarritoPorUsuario(
            @PathVariable Long idUsuario)
            throws CarritoNotFoundException {

        Optional<Carrito> carritoOptional =
                carritoService.obtenerCarritoPorUsuario(idUsuario);

        if (carritoOptional.isEmpty()) {
            throw new CarritoNotFoundException();
        }

        return ResponseEntity.ok(carritoOptional.get());
    }

    @PutMapping("/{idCarrito}/fechas")
    public ResponseEntity<Carrito> modificarFechas(
            @PathVariable Long idCarrito,
            @RequestBody CarritoRequest request)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException {

        Carrito carrito = carritoService.modificarFechas(
                idCarrito,
                request.getIdUsuario(),
                request.getFechaInicio(),
                request.getFechaFin());

        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/{idCarrito}")
    public ResponseEntity<Void> eliminarCarrito(
            @PathVariable Long idCarrito,
            @RequestParam Long idUsuario)
            throws CarritoNotFoundException,
            CarritoInvalidException {

        carritoService.eliminarCarrito(idCarrito, idUsuario);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idCarrito}/continuar")
    public ResponseEntity<Reserva> continuarReserva(
            @PathVariable Long idCarrito,
            @RequestParam Long idUsuario)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException,
            ReservaInvalidException {

        Reserva reserva =
                carritoService.continuarReserva(
                        idCarrito,
                        idUsuario);

        return ResponseEntity
                .created(
                        URI.create(
                                "/reservas/"
                                        + reserva.getIdReserva()))
                .body(reserva);
    }
}
