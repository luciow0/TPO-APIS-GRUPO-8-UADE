package com.uade.tpo.marketplace.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.AgregarCarritoRequest;
import com.uade.tpo.marketplace.dto.CarritoResponse;
import com.uade.tpo.marketplace.dto.ConfirmacionCarritoResponse;
import com.uade.tpo.marketplace.dto.ConfirmarCarritoRequest;
import com.uade.tpo.marketplace.dto.ModificarFechasCarritoRequest;
import com.uade.tpo.marketplace.exception.CarritoDuplicateException;
import com.uade.tpo.marketplace.exception.CarritoInvalidException;
import com.uade.tpo.marketplace.exception.CarritoNotFoundException;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNoDisponibleException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;
import com.uade.tpo.marketplace.service.CarritoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PostMapping("/publicaciones/{idPublicacion}")
    public ResponseEntity<CarritoResponse> agregarPublicacionAlCarrito(
            @PathVariable Long idPublicacion,
            @Valid @RequestBody AgregarCarritoRequest request,
            Authentication authentication)
            throws CarritoInvalidException,
            CarritoDuplicateException,
            PublicacionNotFoundException,
            PublicacionNoDisponibleException,
            UsuarioNotFoundException {

        CarritoResponse carrito =
                carritoService.agregarPublicacionAlCarrito(
                        idPublicacion,
                        authentication.getName(),
                        request);

        return ResponseEntity
                .created(URI.create("/carritos/mio"))
                .body(carrito);
    }

    @GetMapping("/mio")
    public ResponseEntity<CarritoResponse> obtenerMiCarrito(
            Authentication authentication)
            throws UsuarioNotFoundException,
            PublicacionNoDisponibleException {

        Optional<CarritoResponse> carritoOptional =
                carritoService.obtenerMiCarrito(
                        authentication.getName());

        if (carritoOptional.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carritoOptional.get());
    }

    @PutMapping("/{idCarrito}/fechas")
    public ResponseEntity<CarritoResponse> modificarFechas(
            @PathVariable Long idCarrito,
            @Valid @RequestBody ModificarFechasCarritoRequest request)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNoDisponibleException,
            PublicacionNotFoundException {

        CarritoResponse carrito = carritoService.modificarFechas(
                idCarrito,
                request);

        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/{idCarrito}")
    public ResponseEntity<Void> eliminarCarrito(
            @PathVariable Long idCarrito)
            throws CarritoNotFoundException {

        carritoService.eliminarCarrito(idCarrito);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idCarrito}/confirmar")
    public ResponseEntity<ConfirmacionCarritoResponse> confirmarCarrito(
            @PathVariable Long idCarrito,
            @Valid @RequestBody ConfirmarCarritoRequest request)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNoDisponibleException,
            PublicacionNotFoundException,
            ReservaInvalidException,
            ReservaNotFoundException,
            PagoDuplicateException,
            PagoInvalidException {

        ConfirmacionCarritoResponse confirmacion =
                carritoService.confirmarCarrito(
                        idCarrito,
                        request.getMetodoPago());

        return ResponseEntity
                .created(
                        URI.create(
                                "/reservas/"
                                        + confirmacion.getIdReserva()))
                .body(confirmacion);
    }
}
