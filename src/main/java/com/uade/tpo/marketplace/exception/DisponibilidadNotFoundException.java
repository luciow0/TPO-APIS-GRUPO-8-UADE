package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "No encontramos ese período disponible."
)
public class DisponibilidadNotFoundException extends Exception {

    public DisponibilidadNotFoundException() {
        super();
    }

    public DisponibilidadNotFoundException(String mensaje) {
        super(mensaje);
    }
}
