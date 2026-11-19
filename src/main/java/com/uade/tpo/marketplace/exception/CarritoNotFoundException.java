package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "No encontramos tu carrito."
)
public class CarritoNotFoundException extends Exception {

    public CarritoNotFoundException() {
        super();
    }

    public CarritoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
