package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.uade.tpo.marketplace.Enum.RolResena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaPendienteResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiculoResumen {
        private Long idVehiculo;
        private String marca;
        private String modelo;
        private Integer anio;
    }

    private Long idReserva;
    private VehiculoResumen vehiculo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaFinalizacion;
    private UsuarioResumen otraParte;
    // Rol que tiene la otra parte, es decir, el que se va a calificar.
    private RolResena rol;
}
