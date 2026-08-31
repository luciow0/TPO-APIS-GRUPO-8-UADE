package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DisponibilidadRequest {

    private Long idDisponibilidad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long idPublicacion;
}