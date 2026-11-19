package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "Este vehículo ya tiene una publicación activa o pausada. Editá esa en lugar de crear otra."
)
public class PublicacionDuplicateException extends Exception {

    public PublicacionDuplicateException() {
        super();
    }

    public PublicacionDuplicateException(String mensaje) {
        super(mensaje);
    }
}
