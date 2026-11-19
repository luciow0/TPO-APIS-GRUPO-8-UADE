package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "No pudimos actualizar tu carrito. Revisá los datos e intentá de nuevo."
)
public class CarritoInvalidException extends Exception {

    public CarritoInvalidException() {
        super();
    }

    public CarritoInvalidException(String mensaje) {
        super(mensaje);
    }
}
