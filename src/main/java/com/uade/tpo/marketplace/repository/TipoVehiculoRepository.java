package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {
    List<TipoVehiculo> findByTipo(String tipo);
    Boolean existsByTipo(String tipo);
}
