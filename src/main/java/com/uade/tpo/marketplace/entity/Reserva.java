package com.uade.tpo.marketplace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.uade.tpo.marketplace.Enum.EstadoReserva;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;


    @Column(name = "precio_dia_aplicado", precision = 12, scale = 2)
    private BigDecimal precioDiaAplicado;

   @ManyToOne
   @JoinColumn(name = "id_usuario")
    private Usuario cliente;

   @ManyToOne
   @JoinColumn(name = "id_publicacion")
    private Publicacion publicacion;

}
