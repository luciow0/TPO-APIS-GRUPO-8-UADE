package com.uade.tpo.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "El usuario ya posee un carrito vigente"
)
public class CarritoDuplicateException extends Exception {
}
