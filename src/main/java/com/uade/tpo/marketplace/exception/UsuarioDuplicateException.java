package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "Ya existe una cuenta con ese email. Iniciá sesión o usá otro."
)
public class UsuarioDuplicateException extends Exception {

    public UsuarioDuplicateException() {
        super();
    }

    public UsuarioDuplicateException(String mensaje) {
        super(mensaje);
    }
}
