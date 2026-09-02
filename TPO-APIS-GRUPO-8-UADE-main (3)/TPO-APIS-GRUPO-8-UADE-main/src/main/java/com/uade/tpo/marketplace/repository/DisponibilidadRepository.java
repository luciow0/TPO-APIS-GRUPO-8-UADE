package com.uade.tpo.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Disponibilidad;

@Repository
public interface DisponibilidadRepository
        extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByPublicacion_IdPublicacion(
            Long idPublicacion);
}