package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ModificarFechasCarritoRequest {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
