package com.uade.tpo.marketplace.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.Enum.EstadoPago;
import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.entity.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByReservaIdReserva(Long idReserva);

    List<Pago> findByMetodoAndEstadoAndReservaFechaCreacionBefore(
            MetodoPago metodo,
            EstadoPago estado,
            LocalDateTime fechaCreacion);

}
