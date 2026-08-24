package com.uade.tpo.marketplace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Publicacion {

    @Id
    private Long idPublicacion;

   @Column()
   private LocalDate fechaPublicacion;

    private String estado;

    @Column()
    private Double precioDia;

    @Column()
    private Double descuentoPorcentaje;

    private String descripcion;

    @OneToOne
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;


}
