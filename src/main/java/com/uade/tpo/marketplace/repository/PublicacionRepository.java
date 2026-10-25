package com.uade.tpo.marketplace.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.Enum.EstadoReserva;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

        boolean existsByVehiculo_IdVehiculoAndEstadoIn(
                        Long idVehiculo,
                        Collection<EstadoPublicacion> estados);

        List<Publicacion> findByUbicacion_IdUbicacion(Long idUbicacion);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByEstado(EstadoPublicacion estado, Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        Page<Publicacion> findByVehiculo_Propietario_Email(String email, Pageable pageable);

        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        @Query(value = """
                        select p from Publicacion p join p.vehiculo v
                        where v.propietario.email = :emailUsuario
                        and (:estado is null or p.estado = :estado)
                        and (:busqueda is null or lower(concat(v.marca, ' ', v.modelo, ' ', v.patente))
                             like :busqueda escape '!')
                        """, countQuery = """
                        select count(p) from Publicacion p join p.vehiculo v
                        where v.propietario.email = :emailUsuario
                        and (:estado is null or p.estado = :estado)
                        and (:busqueda is null or lower(concat(v.marca, ' ', v.modelo, ' ', v.patente))
                             like :busqueda escape '!')
                        """)
        Page<Publicacion> buscarMisPublicaciones(
                        @Param("emailUsuario") String emailUsuario,
                        @Param("estado") EstadoPublicacion estado,
                        @Param("busqueda") String busqueda,
                        Pageable pageable);

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

        // Catálogo: combina zona, tipo y fechas sobre todas las publicaciones activas.
        // Con fechas, aplica las mismas reglas que agregar al carrito: el rango entra en un
        // período disponible y no se cruza con reservas del vehículo ni con carritos vigentes.
        @EntityGraph(attributePaths = {"vehiculo", "vehiculo.tipoVehiculo", "ubicacion"})
        @Query(value = """
                        select p from Publicacion p join p.vehiculo v join p.ubicacion u
                        where p.estado = :estado
                        and (:zona is null or lower(u.zona) = :zona)
                        and (:idTipoVehiculo is null or v.tipoVehiculo.idTipoVehiculo = :idTipoVehiculo)
                        and (:fechaInicio is null or (
                             exists (select d from Disponibilidad d where d.publicacion = p
                                     and d.fechaInicio <= :fechaInicio and d.fechaFin >= :fechaFin)
                             and not exists (select r from Reserva r where r.publicacion.vehiculo = v
                                     and r.estado in :estadosBloqueantes
                                     and r.fechaInicio <= :fechaFin and r.fechaFin >= :fechaInicio)
                             and not exists (select c from Carrito c where c.publicacion = p
                                     and c.fechaExpiracion > :ahora
                                     and c.fechaInicio <= :fechaFin and c.fechaFin >= :fechaInicio)))
                        order by
                             case when :orden = 'precio-asc' then p.precioDia * (100 - coalesce(p.descuentoPorcentaje, 0)) end asc,
                             case when :orden = 'precio-desc' then p.precioDia * (100 - coalesce(p.descuentoPorcentaje, 0)) end desc,
                             case when :orden = 'anio-desc' then v.anio end desc,
                             p.fechaPublicacion desc, p.idPublicacion desc
                        """, countQuery = """
                        select count(p) from Publicacion p join p.vehiculo v join p.ubicacion u
                        where p.estado = :estado
                        and (:zona is null or lower(u.zona) = :zona)
                        and (:idTipoVehiculo is null or v.tipoVehiculo.idTipoVehiculo = :idTipoVehiculo)
                        and (:fechaInicio is null or (
                             exists (select d from Disponibilidad d where d.publicacion = p
                                     and d.fechaInicio <= :fechaInicio and d.fechaFin >= :fechaFin)
                             and not exists (select r from Reserva r where r.publicacion.vehiculo = v
                                     and r.estado in :estadosBloqueantes
                                     and r.fechaInicio <= :fechaFin and r.fechaFin >= :fechaInicio)
                             and not exists (select c from Carrito c where c.publicacion = p
                                     and c.fechaExpiracion > :ahora
                                     and c.fechaInicio <= :fechaFin and c.fechaFin >= :fechaInicio)))
                        """)
        Page<Publicacion> buscarCatalogo(
                        @Param("estado") EstadoPublicacion estado,
                        @Param("zona") String zona,
                        @Param("idTipoVehiculo") Long idTipoVehiculo,
                        @Param("fechaInicio") LocalDate fechaInicio,
                        @Param("fechaFin") LocalDate fechaFin,
                        @Param("estadosBloqueantes") Collection<EstadoReserva> estadosBloqueantes,
                        @Param("ahora") LocalDateTime ahora,
                        @Param("orden") String orden,
                        Pageable pageable);

        @Query("""
                        select distinct u.zona from Publicacion p join p.ubicacion u
                        where p.estado = :estado and u.zona is not null and u.zona <> ''
                        order by u.zona
                        """)
        List<String> buscarZonas(@Param("estado") EstadoPublicacion estado);
}
