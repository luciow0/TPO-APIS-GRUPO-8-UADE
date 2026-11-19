package com.uade.tpo.marketplace.dto;

import com.uade.tpo.marketplace.Enum.MetodoPago;

import lombok.Data;

@Data
public class ConfirmarCarritoRequest {
    private MetodoPago metodoPago;
}
