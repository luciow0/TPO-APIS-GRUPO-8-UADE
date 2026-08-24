package com.uade.tpo.marketplace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;

    private String url;
    private Integer orden;

   @ManyToOne
   @JoinColumn(name = "id_vehiculo")
   private Vehiculo vehiculo;






}
