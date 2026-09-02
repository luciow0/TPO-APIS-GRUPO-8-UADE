package com.uade.tpo.marketplace.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuario_IdUsuario(Long idUsuario);

    List<Carrito> findByPublicacion_IdPublicacionAndFechaExpiracionAfter(
            Long idPublicacion,
            LocalDateTime fechaActual);
}
