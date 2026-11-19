package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AgregarCarritoRequest {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
