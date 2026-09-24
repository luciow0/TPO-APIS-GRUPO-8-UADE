package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.CONFLICT,
        reason = "La publicacion ya no esta disponible"
)
public class PublicacionNoDisponibleException extends Exception {
}
