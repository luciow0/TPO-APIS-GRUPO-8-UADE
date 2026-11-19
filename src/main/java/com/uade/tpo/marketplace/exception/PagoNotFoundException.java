package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "Esta reserva todavía no tiene un pago registrado."
)
public class PagoNotFoundException extends Exception {

    public PagoNotFoundException() {
        super();
    }

    public PagoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
