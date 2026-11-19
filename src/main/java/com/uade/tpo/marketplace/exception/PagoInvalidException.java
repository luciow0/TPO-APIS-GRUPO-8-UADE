package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "No pudimos procesar el pago. Revisá los datos e intentá de nuevo."
)
public class PagoInvalidException extends Exception {

    public PagoInvalidException() {
        super();
    }

    public PagoInvalidException(String mensaje) {
        super(mensaje);
    }
}
