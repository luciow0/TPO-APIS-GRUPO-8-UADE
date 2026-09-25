package com.uade.tpo.marketplace.entity;

import java.math.BigDecimal;

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
    @NotBlank private String zona;
    @NotBlank
    @Column(name = "codigo_postal")
    private String codigoPostal;

    // Coordenadas para Google Maps. Nullable para las ubicaciones cargadas antes de tenerlas.
    @Column(precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitud;

    // Id del lugar en Google Places (opcional)
    @Column(name = "place_id")
    private String placeId;


}
