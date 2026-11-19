package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "Ya tenés un auto en el carrito. Confirmá esa reserva o quitalo antes de agregar otro."
)
public class CarritoDuplicateException extends Exception {

    public CarritoDuplicateException() {
        super();
    }

    public CarritoDuplicateException(String mensaje) {
        super(mensaje);
    }
}
