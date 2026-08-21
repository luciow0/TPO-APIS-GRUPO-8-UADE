package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Disponibilidad {
    @Id
    private Long idDisponibilidad;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private Publicacion publicacion;

}
