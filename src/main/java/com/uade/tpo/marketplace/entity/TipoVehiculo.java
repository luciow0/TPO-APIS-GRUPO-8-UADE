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
public class TipoVehiculo {

    @Id
    private Long idTipoVehiculo;

    private String nombre;
}
