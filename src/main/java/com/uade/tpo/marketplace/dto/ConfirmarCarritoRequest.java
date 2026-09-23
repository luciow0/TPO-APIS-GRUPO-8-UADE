package com.uade.tpo.marketplace.dto;

import com.uade.tpo.marketplace.Enum.MetodoPago;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmarCarritoRequest {

    @NotNull
    private MetodoPago metodoPago;
}
