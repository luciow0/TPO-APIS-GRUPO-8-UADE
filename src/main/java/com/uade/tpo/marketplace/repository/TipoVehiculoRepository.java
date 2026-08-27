package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {
}
