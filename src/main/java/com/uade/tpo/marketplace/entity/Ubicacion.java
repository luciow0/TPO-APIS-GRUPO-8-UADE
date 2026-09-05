package com.uade.tpo.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUbicacion;

    @NotBlank private String direccion;
    @NotBlank private String ciudad;
    @NotBlank private String provincia;
    @NotBlank private String localidad;
    @NotBlank
    @Column(name = "codigo_postal")
    private String codigoPostal;


}
