package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_GATEWAY,
        reason = "No pudimos conectar con Mercado Pago. Intentá de nuevo."
)
public class MercadoPagoException extends Exception {

    public MercadoPagoException() {
        super();
    }

    public MercadoPagoException(String mensaje) {
        super(mensaje);
    }
}
