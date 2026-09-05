package com.uade.tpo.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UbicacionRequest {
    @NotBlank private String direccion;
    @NotBlank private String ciudad;
    @NotBlank private String provincia;
    @NotBlank private String localidad;
    @NotBlank private String codigoPostal;
    @NotBlank private String zona;
}
