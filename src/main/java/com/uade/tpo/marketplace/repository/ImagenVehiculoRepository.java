package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.ImagenVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenVehiculoRepository extends JpaRepository<ImagenVehiculo, Long> {
}
