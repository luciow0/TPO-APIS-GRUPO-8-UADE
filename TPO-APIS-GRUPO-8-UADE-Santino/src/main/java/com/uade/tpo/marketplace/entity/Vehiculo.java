package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @NotBlank
    @Size(max = 6)
    @Column(nullable = false, unique = true, length = 6)
    private String patente;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String marca;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String modelo;

    @NotNull
    @Min(1900)
    @Max(2026)
    @Column(nullable = false)
    private Integer anio;

    @Size(max = 30)
    private String color;

    @NotNull
    @Min(1)
    @Max(9)
    @Column(name = "cantidad_asientos", nullable = false)
    private Integer cantidadAsientos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario propietario;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "id_tipo_vehiculo", nullable = false)
   private TipoVehiculo tipoVehiculo;

}
