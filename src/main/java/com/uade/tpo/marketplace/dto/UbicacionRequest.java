package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UbicacionRequest {
    @NotBlank private String direccion;
    @NotBlank private String ciudad;
    @NotBlank private String provincia;
    @NotBlank private String localidad;
    @NotBlank private String codigoPostal;
    @NotBlank private String zona;

    // Las envia el front a partir del autocompletado de Google Places
    @NotNull @DecimalMin("-90") @DecimalMax("90")
    private BigDecimal latitud;

    @NotNull @DecimalMin("-180") @DecimalMax("180")
    private BigDecimal longitud;

    private String placeId;
}
