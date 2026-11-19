package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    code = HttpStatus.NOT_FOUND,
    reason = "No encontramos esa reserva."
)
public class ReservaNotFoundException extends Exception {

    public ReservaNotFoundException() {
        super();
    }

    public ReservaNotFoundException(String mensaje) {
        super(mensaje);
    }
}
