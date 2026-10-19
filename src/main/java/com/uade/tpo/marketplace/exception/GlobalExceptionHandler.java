package com.uade.tpo.marketplace.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Hace que todos los errores lleguen al front con el formato {"mensaje": "..."}.
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Errores de @Valid: arma el mensaje con los campos que fallaron.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining(". "));

        if (mensaje.isBlank()) {
            mensaje = "Los datos enviados no son validos";
        }

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Errores propios de Spring MVC (JSON mal formado, metodo no permitido, ruta inexistente, etc.)
    // y ResponseStatusException lanzadas desde los services.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        if (ex instanceof ResponseStatusException rse && rse.getReason() != null) {
            return respuesta(statusCode, rse.getReason());
        }

        return respuesta(statusCode, mensajeGenerico(statusCode));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Credenciales invalidas en el login.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
    }

    // Rechazos de @PreAuthorize.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "No tenes permiso para realizar esta accion");
    }

    // Excepciones propias con @ResponseStatus (usa su reason) y cualquier error inesperado (500).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex) {
        ResponseStatus anotacion =
                AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);

        if (anotacion != null) {
            String mensaje = anotacion.reason().isBlank()
                    ? mensajeGenerico(anotacion.code())
                    : anotacion.reason();
            return respuesta(anotacion.code(), mensaje);
        }

        log.error("Error inesperado", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, mensajeGenerico(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<Object> respuesta(HttpStatusCode status, String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = mensajeGenerico(status);
        }

        return ResponseEntity.status(status).body(Map.of("mensaje", mensaje));
    }

    private String mensajeGenerico(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> "La solicitud no es valida";
            case 401 -> "Tenes que iniciar sesion";
            case 403 -> "No tenes permiso para realizar esta accion";
            case 404 -> "Recurso no encontrado";
            case 405 -> "Metodo no permitido";
            case 409 -> "La operacion entra en conflicto con el estado actual";
            case 413 -> "El archivo es demasiado grande";
            default -> status.is5xxServerError()
                    ? "Ocurrio un error inesperado. Intenta nuevamente mas tarde"
                    : "No se pudo procesar la solicitud";
        };
    }
}
