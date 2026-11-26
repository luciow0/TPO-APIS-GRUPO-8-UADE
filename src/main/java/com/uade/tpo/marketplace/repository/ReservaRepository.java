package com.uade.tpo.marketplace.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
        Page<Reserva> findByClienteIdUsuario(Long idUsuario, Pageable pageable);
        Page<Reserva> findByClienteEmail(String email, Pageable pageable);
        Page<Reserva> findByPublicacionVehiculoPropietarioEmail(String email, Pageable pageable);
        // FINALIZADAS en las que el usuario fue conductor o propietario.
        @Query("select r from Reserva r where r.estado = :estado and "
                        + "(r.cliente.email = :email or r.publicacion.vehiculo.propietario.email = :email)")
        List<Reserva> findDeUsuarioPorEstado(@Param("email") String email, @Param("estado") EstadoReserva estado);
        List<Reserva> findByPublicacion_Vehiculo_IdVehiculoAndEstadoIn(
                        Long idVehiculo,
                        Collection<EstadoReserva> estados);
        boolean existsByClienteIdUsuarioAndEstado(Long idUsuario, EstadoReserva estado);
        // Incluye el historial para poder consultar publicaciones de reservas anteriores.
        boolean existsByClienteEmailAndPublicacionIdPublicacion(
                        String email,
                        Long idPublicacion);
        boolean existsByClienteEmailAndPublicacionIdPublicacionAndEstado(
                        String email,
                        Long idPublicacion,
                        EstadoReserva estado);

}
