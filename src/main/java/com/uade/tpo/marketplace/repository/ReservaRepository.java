package com.uade.tpo.marketplace.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
        Page<Reserva> findByClienteIdUsuario(Long idUsuario, Pageable pageable);
        List<Reserva> findByPublicacionIdPublicacionAndEstado(Long idPublicacion,EstadoReserva estado);
        boolean existsByClienteIdUsuarioAndEstado(Long idUsuario, EstadoReserva estado);

}
