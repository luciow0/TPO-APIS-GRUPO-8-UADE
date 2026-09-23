package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {

    private Long idCarrito;
    private Long idPublicacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaExpiracion;
    private BigDecimal precioDiaAplicado;
    private Long cantidadDias;
    private BigDecimal precioTotal;
}
