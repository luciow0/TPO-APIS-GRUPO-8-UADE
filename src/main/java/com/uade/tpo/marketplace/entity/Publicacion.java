package com.uade.tpo.marketplace.entity;

import com.uade.tpo.marketplace.enums.EstadoPublicacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPublicacion;

    private LocalDate fechaPublicacion;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estado;

    @Column(precision = 12, scale = 2)
    private BigDecimal precioDia;

    @Column(precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    private String descripcion;

    // Hora única fijada por el propietario para retiro y devolución.
    private LocalTime horaRetiroDevolucion;

    @OneToOne
    @JoinColumn(name = "id_vehiculo", unique = true, nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Ubicacion ubicacion;
}
