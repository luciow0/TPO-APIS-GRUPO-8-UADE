package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.uade.tpo.marketplace.Enum.EstadoReserva;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {

    private Long idReserva;
    private Long idPublicacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaCreacion;
    private EstadoReserva estado;
    private BigDecimal precioDiaAplicado;
    private Long cantidadDias;
    private BigDecimal precioTotal;
}
