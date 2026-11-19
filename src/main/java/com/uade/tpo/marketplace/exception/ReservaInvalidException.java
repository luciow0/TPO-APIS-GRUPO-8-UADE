package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "No pudimos actualizar la reserva. Revisá los datos e intentá de nuevo."
)
public class ReservaInvalidException extends Exception {

    public ReservaInvalidException() {
        super();
    }

    public ReservaInvalidException(String mensaje) {
        super(mensaje);
    }
}
