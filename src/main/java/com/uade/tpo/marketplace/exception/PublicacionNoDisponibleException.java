package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.CONFLICT,
        reason = "Esta publicación no está disponible para reservar en este momento."
)
public class PublicacionNoDisponibleException extends Exception {

    public PublicacionNoDisponibleException() {
        super();
    }

    public PublicacionNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
