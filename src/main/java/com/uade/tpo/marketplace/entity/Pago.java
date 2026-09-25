package com.uade.tpo.marketplace.entity;
import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pago {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPago;
    private LocalDate fecha;
    @Column(precision = 12, scale = 2)
    private BigDecimal monto;
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;
    @Enumerated(EnumType.STRING)
    private MetodoPago metodo;
    private String preferenceId;
    private String mercadoPagoPaymentId;

    @OneToOne
    @JoinColumn(name = "id_reserva", nullable = false, unique = true)
    private Reserva reserva;

}
