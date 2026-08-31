package com.uade.tpo.marketplace.entity;

import com.uade.tpo.marketplace.Enum.MetodoPago;
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
public class Pago {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPago;
    private LocalDate fecha;
    private float monto;
    @Enumerated(EnumType.STRING)
    private MetodoPago metodo;
    private boolean estado; // pendiente false, pago true

}
