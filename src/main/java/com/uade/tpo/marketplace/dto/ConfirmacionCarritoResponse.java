package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;

import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.EstadoReserva;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionCarritoResponse {

    private Long idReserva;
    private EstadoReserva estadoReserva;
    private Long idPago;
    private EstadoPago estadoPago;
    private BigDecimal monto;
}
