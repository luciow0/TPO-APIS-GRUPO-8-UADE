package com.uade.tpo.marketplace.dto;

import lombok.Data;

/**
 * Datos de entrada para crear/actualizar un Vehiculo.
 * Reemplaza a la entidad Vehiculo como @RequestBody: el cliente nunca
 * deserializa directamente sobre la entidad JPA (evita, por ejemplo, que
 * mande "imagenes" y Hibernate intente persistirlas sueltas por el cascade).
 */
@Data
public class VehiculoRequest {
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private Integer cantidadAsientos;
    private Long idUsuario;

    // Puede ser null: el vehiculo puede no tener tipo asignado
    private Long idTipoVehiculo;
}
