package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ubicacion {

    @Id
    private Long idUbicacion;

    private String direccion;
    private String zona;
    private String provincia;
    private String localidad;

    //@Column(name = "codigo_postal")
    private String codigoPostal;


}
