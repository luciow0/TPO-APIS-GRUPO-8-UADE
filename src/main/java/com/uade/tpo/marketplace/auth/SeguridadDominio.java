package com.uade.tpo.marketplace.auth;

import java.util.Optional;

import com.uade.tpo.marketplace.exception.CarritoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.repository.CarritoRepository;
import com.uade.tpo.marketplace.repository.PublicacionRepository;
import com.uade.tpo.marketplace.repository.ReservaRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import com.uade.tpo.marketplace.repository.VehiculoRepository;

@Component("seguridadDominio")
public class SeguridadDominio {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private PublicacionRepository publicacionRepository;
    @Autowired private VehiculoRepository vehiculoRepository;

    public boolean esMismoUsuario(Authentication auth, Long idUsuarioRequest) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuarioRequest);
        return usuario.isPresent() && usuario.get().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeCarrito(Authentication auth, Long idCarrito) throws CarritoNotFoundException {
        // 1. Validar existencia: Si no existe, rompemos el flujo con tu excepción (genera un 404)
        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new CarritoNotFoundException());

        // 2. Validar propiedad: Si existe, evaluamos la autorización (false genera un 403)
        return carrito.getUsuario().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeReserva(Authentication auth, Long idReserva) {
        Optional<Reserva> reserva = reservaRepository.findById(idReserva);
        return reserva.isPresent() && reserva.get().getCliente().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDePublicacion(Authentication auth, Long idPublicacion) {
        Optional<Publicacion> publicacion = publicacionRepository.findById(idPublicacion);
        return publicacion.isPresent()
                && publicacion.get().getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeVehiculo(Authentication auth, Long idVehiculo) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(idVehiculo);
        return vehiculo.isPresent() && vehiculo.get().getPropietario().getEmail().equals(auth.getName());
    }
}