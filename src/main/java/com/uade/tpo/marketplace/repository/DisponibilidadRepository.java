package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
}
