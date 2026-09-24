package com.uade.tpo.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.uade.tpo.marketplace.Enum.EstadoPublicacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionResponse {

    private Long idPublicacion;
    private LocalDate fechaPublicacion;
    private EstadoPublicacion estado;
    private BigDecimal precioDia;
    private BigDecimal descuentoPorcentaje;
    private String descripcion;
    private LocalTime horaRetiroDevolucion;
    private Long idVehiculo;
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private Integer cantidadAsientos;
    private Long idTipoVehiculo;
    private String tipoVehiculo;
    private Long idUbicacion;
    private String provincia;
    private String ciudad;
    private String localidad;
    private String zona;
}
