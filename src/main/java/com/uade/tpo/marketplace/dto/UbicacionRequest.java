package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UbicacionRequest {
    private String direccion;
    private String ciudad;
    private String provincia;
    private String localidad;
    private String codigoPostal;
    private String zona;

    // Las envia el front a partir del autocompletado de Google Places
    private BigDecimal latitud;

    private BigDecimal longitud;

    private String placeId;
}
