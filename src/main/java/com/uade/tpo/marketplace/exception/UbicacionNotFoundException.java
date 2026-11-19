package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "No encontramos esa ubicación."
)
public class UbicacionNotFoundException extends Exception {

    public UbicacionNotFoundException() {
        super();
    }

    public UbicacionNotFoundException(String mensaje) {
        super(mensaje);
    }
}
