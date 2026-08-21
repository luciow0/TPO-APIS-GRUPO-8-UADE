package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Vehiculo {

    @Id
    private Long idVehiculo;

    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;

    //@Column(name = "cantidad_asientos")
    private Integer cantidadAsientos;

   // @ManyToOne
   // @JoinColumn(name = "id_usuario")
    private Usuario propietario;

   // @ManyToOne
   // @JoinColumn(name = "id_tipo_vehiculo")
    private TipoVehiculo tipoVehiculo;

}
