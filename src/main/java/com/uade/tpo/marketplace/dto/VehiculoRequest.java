package com.uade.tpo.marketplace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos de entrada para crear/actualizar un Vehiculo.
 * Reemplaza a la entidad Vehiculo como @RequestBody: el cliente nunca
 * deserializa directamente sobre la entidad JPA (evita, por ejemplo, que
 * mande "imagenes" y Hibernate intente persistirlas sueltas por el cascade).
 */
@Data
public class VehiculoRequest {

    @NotBlank
    @Size(max = 6)
    private String patente;

    @NotBlank
    @Size(max = 50)
    private String marca;

    @NotBlank
    @Size(max = 50)
    private String modelo;

    @NotNull
    @Min(1900)
    @Max(value = 2026, message = "El año no puede ser mayor al año actual")
    private Integer anio;

    @NotBlank
    @Size(max = 30)
    private String color;

    @NotNull
    @Min(1)
    @Max(9)
    private Integer cantidadAsientos;

    @NotNull
    private Long idUsuario;

    // Puede ser null: el vehiculo puede no tener tipo asignado
    private Long idTipoVehiculo;
}
