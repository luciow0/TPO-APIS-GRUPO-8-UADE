package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// La lanzan las validaciones de ValidacionVolanta cuando un dato que manda el front no es válido.
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DatoInvalidoException extends RuntimeException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
