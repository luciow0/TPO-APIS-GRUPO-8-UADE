package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.MetodoPago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {

    private Long idPago;
    private Long idReserva;
    private LocalDate fecha;
    private BigDecimal monto;
    private EstadoPago estado;
    private MetodoPago metodo;
}
