package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_GATEWAY,
        reason = "Error al comunicarse con Mercado Pago"
)
public class MercadoPagoException extends Exception {
}
