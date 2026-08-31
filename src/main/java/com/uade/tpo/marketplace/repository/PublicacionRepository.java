package com.uade.tpo.marketplace.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.enums.EstadoPublicacion;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    boolean existsByVehiculo_IdVehiculo(Long idVehiculo);

    List<Publicacion> findByEstado(EstadoPublicacion estado);

    List<Publicacion> findByPrecioDiaBetween(
            BigDecimal precioMin,
            BigDecimal precioMax);

    List<Publicacion> findByVehiculo_TipoVehiculo_IdTipoVehiculo(
            Long idTipoVehiculo);

    List<Publicacion> findByVehiculo_MarcaIgnoreCase(
            String marca);

    List<Publicacion> findByVehiculo_ModeloIgnoreCase(
            String modelo);

    List<Publicacion> findByUbicacion_ZonaIgnoreCase(
            String zona);
}