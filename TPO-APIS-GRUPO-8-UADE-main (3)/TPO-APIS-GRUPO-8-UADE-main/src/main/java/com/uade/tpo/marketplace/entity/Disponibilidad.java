package com.uade.tpo.marketplace.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Disponibilidad {

    public Disponibilidad() {
    }

    public Disponibilidad(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Publicacion publicacion) {

        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.publicacion = publicacion;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDisponibilidad;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "id_publicacion", nullable = false)
    private Publicacion publicacion;
}