package com.uade.tpo.marketplace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUbicacion;

    private String direccion;
    private String ciudad;
    private String provincia;
    private String localidad;

    @Column(name = "codigo_postal")
    private String codigoPostal;


}
