package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Reserva {

    @Id
    private Long idReserva;

   // @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

   // @Column(name = "fecha_fin")
    private LocalDate fechaFin;

   // @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private String estado;

  //  @Column(name = "precio_dia_aplicado")
    private Double precioDiaAplicado;

   // @ManyToOne
   //@JoinColumn(name = "id_usuario")
    private Usuario cliente;

   // @ManyToOne
   // @JoinColumn(name = "id_publicacion")
    private Publicacion publicacion;

}
