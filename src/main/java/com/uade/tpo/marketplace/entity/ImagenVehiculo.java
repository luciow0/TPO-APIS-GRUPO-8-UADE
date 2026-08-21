package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenVehiculo {

    @Id
    private Long idImagen;

    private String url;
    private Integer orden;

   //@ManyToOne
   //@JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;





}
