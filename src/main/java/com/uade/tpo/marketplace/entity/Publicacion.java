package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Publicacion {

    @Id
    private Long idPublicacion;

   // @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    private String estado;

    //@Column(name = "precio_dia")
    private Double precioDia;

  //  @Column(name = "descuento_porcentaje")
    private Double descuentoPorcentaje;

    private String descripcion;

   // @ManyToOne
   // @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

//    @ManyToOne
   // @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;


}
