package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 401 con mensaje listo para mostrar (el handler genérico usa el mensaje del throw).
@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "La contraseña no es correcta.")
public class ContrasenaIncorrectaException extends RuntimeException {

    public ContrasenaIncorrectaException(String mensaje) {
        super(mensaje);
    }
}
