package com.uade.tpo.marketplace.repository;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Carrito c where c.idCarrito = :id")
    Optional<Carrito> buscarParaModificar(@Param("id") Long id);

    Optional<Carrito> findByUsuario_IdUsuario(Long idUsuario);

    List<Carrito> findByPublicacion_IdPublicacionAndFechaExpiracionAfter(
            Long idPublicacion,
            LocalDateTime fechaActual);
}
