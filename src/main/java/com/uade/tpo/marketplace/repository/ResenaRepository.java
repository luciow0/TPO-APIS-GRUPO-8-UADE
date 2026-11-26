package com.uade.tpo.marketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.Enum.RolResena;
import com.uade.tpo.marketplace.entity.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    boolean existsByReservaIdReservaAndAutorEmail(Long idReserva, String email);

    // Como hay una sola reseña por autor y reserva, otra reseña de la reserva es de la otra parte.
    boolean existsByReservaIdReservaAndIdResenaNot(Long idReserva, Long idResena);

    Optional<Resena> findByReservaIdReservaAndAutorEmail(Long idReserva, String email);

    Optional<Resena> findByReservaIdReservaAndDestinatarioEmail(Long idReserva, String email);

    List<Resena> findByAutorEmailOrderByFechaDesc(String email);

    List<Resena> findByDestinatarioIdUsuarioAndRolOrderByFechaDesc(Long idUsuario, RolResena rol);
}
