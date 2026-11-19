package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "No encontramos esa cuenta."
)
public class UsuarioNotFoundException extends Exception {

    public UsuarioNotFoundException() {
        super();
    }

    public UsuarioNotFoundException(String mensaje) {
        super(mensaje);
    }
}
