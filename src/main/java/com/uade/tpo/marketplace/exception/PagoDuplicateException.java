package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "Esta reserva ya tiene un pago registrado."
)
public class PagoDuplicateException extends Exception {

    public PagoDuplicateException() {
        super();
    }

    public PagoDuplicateException(String mensaje) {
        super(mensaje);
    }
}
