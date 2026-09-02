package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CarritoRequest {

    private Long idUsuario;
    private Long idPublicacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
