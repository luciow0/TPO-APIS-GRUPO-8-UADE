package com.uade.tpo.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {

    private Long idVehiculo;
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private Integer cantidadAsientos;

    // Del propietario solo exponemos el id (no el objeto Usuario entero)
    private Long idUsuario;

    // Tipo de vehiculo (puede ser null si el vehiculo no tiene tipo asignado)
    private Long idTipoVehiculo;
    private String tipoVehiculo;
}
