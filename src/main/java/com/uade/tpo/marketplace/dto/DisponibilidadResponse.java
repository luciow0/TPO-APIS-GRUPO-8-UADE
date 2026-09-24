package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadResponse {

    private Long idDisponibilidad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long idPublicacion;
}
