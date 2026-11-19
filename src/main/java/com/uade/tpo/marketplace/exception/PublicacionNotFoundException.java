package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "No encontramos esa publicación."
)
public class PublicacionNotFoundException extends Exception {

    public PublicacionNotFoundException() {
        super();
    }

    public PublicacionNotFoundException(String mensaje) {
        super(mensaje);
    }
}
