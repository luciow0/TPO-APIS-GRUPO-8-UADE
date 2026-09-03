package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "La operacion sobre la reserva no es valida"
)
public class ReservaInvalidException extends Exception {

}
