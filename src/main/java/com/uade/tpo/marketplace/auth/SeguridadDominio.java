package com.uade.tpo.marketplace.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.Enum.EstadoReserva;
import com.uade.tpo.marketplace.entity.*;
import com.uade.tpo.marketplace.exception.*;
import com.uade.tpo.marketplace.repository.*;

@Component("seguridadDominio")
public class SeguridadDominio {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private PublicacionRepository publicacionRepository;
    @Autowired private VehiculoRepository vehiculoRepository;
    @Autowired private PagoRepository pagoRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private ImagenVehiculoRepository imagenVehiculoRepository;
    @Autowired private UbicacionRepository ubicacionRepository;

    public boolean esMismoUsuario(Authentication auth, Long idUsuarioRequest) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(idUsuarioRequest)
                .orElseThrow(UsuarioNotFoundException::new);
        return usuario.getEmail().equals(auth.getName());
    }

    public boolean esDuenioDeCarrito(Authentication auth, Long idCarrito) throws CarritoNotFoundException {
        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(CarritoNotFoundException::new);
        return carrito.getUsuario().getEmail().equals(auth.getName());
    }

    public boolean esDuenioDeReserva(Authentication auth, Long idReserva) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(ReservaNotFoundException::new);
        return reserva.getCliente().getEmail().equals(auth.getName());
    }

    // El propietario del auto reservado (no el conductor).
    public boolean esPropietarioDeReserva(Authentication auth, Long idReserva) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(ReservaNotFoundException::new);
        return reserva.getPublicacion().getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }

    public boolean esDuenioDePublicacion(Authentication auth, Long idPublicacion) throws PublicacionNotFoundException {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(PublicacionNotFoundException::new);
        return publicacion.getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }

    // Una publicacion pausada o dada de baja solo la ven el admin, el dueño y quien la reservó.
    // A los demas se les avisa que ya no está disponible (en lugar del 403 genérico).
    public boolean puedeVerPublicacion(Authentication auth, Long idPublicacion)
            throws PublicacionNotFoundException, PublicacionNoDisponibleException {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(PublicacionNotFoundException::new);

        if (publicacion.getEstado() == EstadoPublicacion.ACTIVA) {
            return true;
        }

        boolean logueado = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        boolean puedeVer = logueado && (auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                || publicacion.getVehiculo().getPropietario().getEmail().equals(auth.getName())
                || reservaRepository.existsByClienteEmailAndPublicacionIdPublicacion(
                        auth.getName(), idPublicacion));

        if (!puedeVer) {
            throw new PublicacionNoDisponibleException("Esta publicación ya no está disponible.");
        }

        return true;
    }

    // Dirección y coordenadas exactas: solo el dueño o quien tiene una reserva confirmada
    public boolean puedeVerUbicacionExacta(Authentication auth, Publicacion publicacion) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String email = auth.getName();
        return publicacion.getVehiculo().getPropietario().getEmail().equals(email)
                || reservaRepository.existsByClienteEmailAndPublicacionIdPublicacionAndEstado(
                        email, publicacion.getIdPublicacion(), EstadoReserva.CONFIRMADA);
    }

    // La patente solo la ve el dueño del vehículo o un admin; en el catálogo y el detalle público va en null.
    public boolean puedeVerPatente(Authentication auth, Vehiculo vehiculo) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                || (vehiculo.getPropietario() != null
                        && vehiculo.getPropietario().getEmail().equals(auth.getName()));
    }

    // GET /ubicaciones/{id}: el admin, o quien puede ver la ubicación exacta de alguna publicación que la usa
    public boolean puedeVerUbicacion(Authentication auth, Long idUbicacion) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        if (auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        boolean esCreador = ubicacionRepository.findById(idUbicacion)
                .map(ubicacion -> ubicacion.getCreador() != null
                        && ubicacion.getCreador().getEmail().equals(auth.getName()))
                .orElse(false);
        return esCreador || publicacionRepository.findByUbicacion_IdUbicacion(idUbicacion).stream()
                .anyMatch(publicacion -> puedeVerUbicacionExacta(auth, publicacion));
    }

    public boolean esDuenioDeVehiculo(Authentication auth, Long idVehiculo) {
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new EntityNotFoundException("No encontramos ese vehículo."));
        return vehiculo.getPropietario().getEmail().equals(auth.getName());
    }

    public boolean esDuenioDePago(Authentication auth, Long idPago) throws PagoNotFoundException {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PagoNotFoundException("No encontramos ese pago."));
        return pago.getReserva().getCliente().getEmail().equals(auth.getName());
    }

    public boolean puedeVerDisponibilidad(Authentication auth, Long idDisponibilidad)
            throws DisponibilidadNotFoundException, PublicacionNotFoundException, PublicacionNoDisponibleException {
        Disponibilidad disponibilidad = disponibilidadRepository.findById(idDisponibilidad)
                .orElseThrow(DisponibilidadNotFoundException::new);
        return puedeVerPublicacion(auth, disponibilidad.getPublicacion().getIdPublicacion());
    }

    public boolean esDuenioDeDisponibilidad(Authentication auth, Long idDisponibilidad) throws DisponibilidadNotFoundException {
        Disponibilidad disp = disponibilidadRepository.findById(idDisponibilidad)
                .orElseThrow(DisponibilidadNotFoundException::new);
        return disp.getPublicacion().getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }

    public boolean esDuenioDeImagen(Authentication auth, Long idImagen) {
        ImagenVehiculo img = imagenVehiculoRepository.findById(idImagen)
                .orElseThrow(() -> new EntityNotFoundException("No encontramos esa foto."));
        return img.getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }
}
