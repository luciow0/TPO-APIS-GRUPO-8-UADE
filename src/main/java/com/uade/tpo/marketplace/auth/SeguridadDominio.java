package com.uade.tpo.marketplace.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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

    public boolean esMismoUsuario(Authentication auth, Long idUsuarioRequest) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(idUsuarioRequest)
                .orElseThrow(UsuarioNotFoundException::new);
        return usuario.getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeCarrito(Authentication auth, Long idCarrito) throws CarritoNotFoundException {
        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(CarritoNotFoundException::new);
        return carrito.getUsuario().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeReserva(Authentication auth, Long idReserva) throws ReservaNotFoundException {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(ReservaNotFoundException::new);
        return reserva.getCliente().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDePublicacion(Authentication auth, Long idPublicacion) throws PublicacionNotFoundException {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(PublicacionNotFoundException::new);
        return publicacion.getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeVehiculo(Authentication auth, Long idVehiculo) {
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con id: " + idVehiculo));
        return vehiculo.getPropietario().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDePago(Authentication auth, Long idPago) throws PagoNotFoundException {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(PagoNotFoundException::new);
        return pago.getReserva().getCliente().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeDisponibilidad(Authentication auth, Long idDisponibilidad) throws DisponibilidadNotFoundException {
        Disponibilidad disp = disponibilidadRepository.findById(idDisponibilidad)
                .orElseThrow(DisponibilidadNotFoundException::new);
        return disp.getPublicacion().getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }

    public boolean esDueñoDeImagen(Authentication auth, Long idImagen) {
        ImagenVehiculo img = imagenVehiculoRepository.findById(idImagen)
                .orElseThrow(() -> new EntityNotFoundException("Imagen no encontrada con id: " + idImagen));
        return img.getVehiculo().getPropietario().getEmail().equals(auth.getName());
    }
}