package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {
    Optional<TipoVehiculo> findByNombre(String nombre);
    Boolean existsByNombre(String nombre);
}