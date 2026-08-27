package com.uade.tpo.marketplace.entity;

import com.uade.tpo.marketplace.enums.EstadoPago;
import com.uade.tpo.marketplace.enums.MetodoPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    private LocalDate fechaPago;

    @Column(precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @OneToOne
    @JoinColumn(name = "id_reserva", unique = true, nullable = false)
    private Reserva reserva;
}
