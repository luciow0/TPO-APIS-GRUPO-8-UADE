package com.uade.tpo.marketplace.repository;

import java.math.BigDecimal;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.Enum.EstadoPublicacion;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

        boolean existsByVehiculo_IdVehiculoAndEstadoIn(
                        Long idVehiculo,
                        Collection<EstadoPublicacion> estados);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByEstado(EstadoPublicacion estado, Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByVehiculo_Propietario_Email(String email, Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByPrecioDiaBetweenAndEstado(
                        BigDecimal precioMin,
                        BigDecimal precioMax,
                        EstadoPublicacion estado,
                        Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByVehiculo_TipoVehiculo_IdTipoVehiculoAndEstado(
                        Long idTipoVehiculo,
                        EstadoPublicacion estado,
                        Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByVehiculo_MarcaIgnoreCaseAndEstado(
                        String marca,
                        EstadoPublicacion estado,
                        Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByVehiculo_ModeloIgnoreCaseAndEstado(
                        String modelo,
                        EstadoPublicacion estado,
                        Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByUbicacion_ProvinciaIgnoreCaseAndEstado(
                        String provincia,
                        EstadoPublicacion estado,
                        Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByUbicacion_ProvinciaIgnoreCaseAndUbicacion_CiudadIgnoreCaseAndEstado(
                        String provincia,
                        String ciudad,
                        EstadoPublicacion estado,
                        Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByUbicacion_ProvinciaIgnoreCaseAndUbicacion_CiudadIgnoreCaseAndUbicacion_LocalidadIgnoreCaseAndEstado(
                        String provincia,
                        String ciudad,
                        String localidad,
                        EstadoPublicacion estado,
                        Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByUbicacion_ZonaIgnoreCaseAndEstado(
                String zona,
                EstadoPublicacion estado,
                Pageable pageable);
}
