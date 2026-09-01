package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Data;

@Data
public class PublicacionRequest {

    private Long idPublicacion;
    private BigDecimal precioDia;
    private BigDecimal descuentoPorcentaje;
    private String descripcion;
    private LocalTime horaRetiroDevolucion;
    private Long idVehiculo;
    private Long idUbicacion;
}