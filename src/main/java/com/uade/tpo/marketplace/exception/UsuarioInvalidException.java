package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "La operacion sobre el usuario no es valida"
)
public class UsuarioInvalidException extends Exception {
}
