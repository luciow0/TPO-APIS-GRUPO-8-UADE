package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.dto.AgregarCarritoRequest;
import com.uade.tpo.marketplace.dto.CarritoResponse;
import com.uade.tpo.marketplace.dto.ConfirmacionCarritoResponse;
import com.uade.tpo.marketplace.dto.ModificarFechasCarritoRequest;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Disponibilidad;
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exception.CarritoDuplicateException;
import com.uade.tpo.marketplace.exception.CarritoInvalidException;
import com.uade.tpo.marketplace.exception.CarritoNotFoundException;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNoDisponibleException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;
import com.uade.tpo.marketplace.repository.CarritoRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private DisponibilidadService disponibilidadService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PagoService pagoService;

    @PreAuthorize("isAuthenticated()")
    @Override
    public CarritoResponse agregarPublicacionAlCarrito(
            Long idPublicacion,
            String emailUsuario,
            AgregarCarritoRequest request)
            throws CarritoInvalidException,
            CarritoDuplicateException,
            PublicacionNotFoundException,
            PublicacionNoDisponibleException,
            UsuarioNotFoundException {

        validarRequest(idPublicacion, request);

        Usuario usuario =
                usuarioService.obtenerUsuarioPorEmail(
                        emailUsuario);

        validarCarritoDelUsuario(usuario.getIdUsuario());

        Publicacion publicacion =
                publicacionService.obtenerPublicacionPorId(
                        idPublicacion);

        validarPublicacionActiva(publicacion);

        validarDisponibilidad(
                publicacion,
                request.getFechaInicio(),
                request.getFechaFin());

        validarReservasBloqueantes(
                publicacion.getVehiculo().getIdVehiculo(),
                request.getFechaInicio(),
                request.getFechaFin());

        validarSolapamientoCarritos(
                publicacion.getIdPublicacion(),
                request.getFechaInicio(),
                request.getFechaFin(),
                null);

        LocalDateTime fechaCreacion = LocalDateTime.now();

        Carrito carrito = new Carrito();

        carrito.setUsuario(usuario);
        carrito.setPublicacion(publicacion);
        carrito.setFechaInicio(request.getFechaInicio());
        carrito.setFechaFin(request.getFechaFin());
        carrito.setFechaCreacion(fechaCreacion);
        carrito.setFechaExpiracion(fechaCreacion.plusMinutes(15));
        carrito.setPrecioDiaAplicado(
                calcularPrecioDiaConDescuento(publicacion));

        return convertirAResponse(
                carritoRepository.save(carrito));
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Optional<CarritoResponse> obtenerMiCarrito(String emailUsuario)
            throws UsuarioNotFoundException,
            PublicacionNoDisponibleException {

        Usuario usuario =
                usuarioService.obtenerUsuarioPorEmail(emailUsuario);

        Optional<Carrito> carritoOptional =
                carritoRepository.findByUsuario_IdUsuario(
                        usuario.getIdUsuario());

        if (carritoOptional.isEmpty()) {
            return Optional.empty();
        }

        Carrito carrito = carritoOptional.get();

        if (!carrito.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            carritoRepository.delete(carrito);
            return Optional.empty();
        }

        validarPublicacionDelCarrito(carrito);

        return Optional.of(convertirAResponse(carrito));
    }

    @PreAuthorize("@seguridadDominio.esDuenioDeCarrito(authentication, #idCarrito)")
    @Override
    public CarritoResponse modificarFechas(
            Long idCarrito,
            ModificarFechasCarritoRequest request)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNoDisponibleException,
            PublicacionNotFoundException {

        Optional<Carrito> carritoOptional =
                carritoRepository.findById(idCarrito);

        if (carritoOptional.isEmpty()) {
            throw new CarritoNotFoundException();
        }

        Carrito carrito = carritoOptional.get();

        if (request == null) {
            throw new CarritoInvalidException();
        }

        if (!carrito.getFechaExpiracion()
                .isAfter(LocalDateTime.now())) {

            carritoRepository.delete(carrito);
            throw new CarritoNotFoundException();
        }

        validarFechas(
                request.getFechaInicio(),
                request.getFechaFin());

        Publicacion publicacion = carrito.getPublicacion();

        validarPublicacionDelCarrito(carrito);

        validarDisponibilidad(
                publicacion,
                request.getFechaInicio(),
                request.getFechaFin());

        validarReservasBloqueantes(
                publicacion.getVehiculo().getIdVehiculo(),
                request.getFechaInicio(),
                request.getFechaFin());

        validarSolapamientoCarritos(
                publicacion.getIdPublicacion(),
                request.getFechaInicio(),
                request.getFechaFin(),
                idCarrito);

        carrito.setFechaInicio(request.getFechaInicio());
        carrito.setFechaFin(request.getFechaFin());

        return convertirAResponse(
                carritoRepository.save(carrito));
    }

    @PreAuthorize("@seguridadDominio.esDuenioDeCarrito(authentication, #idCarrito)")
    @Override
    public void eliminarCarrito(Long idCarrito)
            throws CarritoNotFoundException {

        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(CarritoNotFoundException::new);

        // La propiedad del carrito ya fue validada por @PreAuthorize.
        carritoRepository.delete(carrito);
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = PublicacionNoDisponibleException.class)
    @PreAuthorize("@seguridadDominio.esDuenioDeCarrito(authentication, #idCarrito)")
    public ConfirmacionCarritoResponse confirmarCarrito(
            Long idCarrito,
            MetodoPago metodoPago)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNoDisponibleException,
            PublicacionNotFoundException,
            ReservaInvalidException,
            ReservaNotFoundException,
            PagoDuplicateException,
            PagoInvalidException {

        if (metodoPago == null) {
            throw new PagoInvalidException();
        }

        Optional<Carrito> carritoOptional =
                carritoRepository.findById(idCarrito);

        if (carritoOptional.isEmpty()) {
            throw new CarritoNotFoundException();
        }

        Carrito carrito = carritoOptional.get();

        if (!carrito.getFechaExpiracion()
                .isAfter(LocalDateTime.now())) {

            throw new CarritoNotFoundException();
        }

        validarFechas(
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        Publicacion publicacion = carrito.getPublicacion();

        validarPublicacionDelCarrito(carrito);

        validarDisponibilidad(
                publicacion,
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        validarReservasBloqueantes(
                publicacion.getVehiculo().getIdVehiculo(),
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        validarSolapamientoCarritos(
                publicacion.getIdPublicacion(),
                carrito.getFechaInicio(),
                carrito.getFechaFin(),
                carrito.getIdCarrito());

        Reserva reserva =
                reservaService.crearReservaDesdeCarrito(carrito);

        Pago pago =
                pagoService.crearPago(
                        reserva.getIdReserva(),
                        metodoPago);

        carritoRepository.delete(carrito);

        return new ConfirmacionCarritoResponse(
                reserva.getIdReserva(),
                reserva.getEstado(),
                pago.getIdPago(),
                pago.getEstado(),
                pago.getMonto());
    }

    private void validarFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws CarritoInvalidException {

        if (fechaInicio == null || fechaFin == null) {
            throw new CarritoInvalidException();
        }

        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new CarritoInvalidException();
        }

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new CarritoInvalidException();
        }
    }

    private void validarDisponibilidad(
            Publicacion publicacion,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws PublicacionNotFoundException,
            CarritoInvalidException {

        List<Disponibilidad> disponibilidades =
                disponibilidadService
                        .obtenerDisponibilidadesPorPublicacion(
                                publicacion.getIdPublicacion());

        boolean fechasDisponibles = false;

        for (Disponibilidad disponibilidad : disponibilidades) {

            boolean inicioValido =
                    !fechaInicio.isBefore(
                            disponibilidad.getFechaInicio());

            boolean finValido =
                    !fechaFin.isAfter(
                            disponibilidad.getFechaFin());

            if (inicioValido && finValido) {
                fechasDisponibles = true;
                break;
            }
        }

        if (!fechasDisponibles) {
            throw new CarritoInvalidException();
        }
    }

    private void validarPublicacionActiva(Publicacion publicacion)
            throws PublicacionNoDisponibleException {

        if (publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
            throw new PublicacionNoDisponibleException();
        }
    }

    private void validarPublicacionDelCarrito(Carrito carrito)
            throws PublicacionNoDisponibleException {

        if (carrito.getPublicacion().getEstado()
                != EstadoPublicacion.ACTIVA) {

            carritoRepository.delete(carrito);
            throw new PublicacionNoDisponibleException();
        }
    }

    private void validarCarritoDelUsuario(Long idUsuario)
            throws CarritoDuplicateException {

        Optional<Carrito> carritoOptional =
                carritoRepository.findByUsuario_IdUsuario(idUsuario);

        if (carritoOptional.isEmpty()) {
            return;
        }

        Carrito carritoExistente = carritoOptional.get();

        boolean carritoExpirado =
                !carritoExistente.getFechaExpiracion()
                        .isAfter(LocalDateTime.now());

        boolean publicacionNoDisponible =
                carritoExistente.getPublicacion().getEstado()
                        != EstadoPublicacion.ACTIVA;

        if (carritoExpirado || publicacionNoDisponible) {
            carritoRepository.delete(carritoExistente);
            return;
        }

        throw new CarritoDuplicateException();
    }

    private void validarSolapamientoCarritos(
            Long idPublicacion,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idCarritoIgnorado)
            throws CarritoInvalidException {

        List<Carrito> carritosVigentes =
                carritoRepository
                        .findByPublicacion_IdPublicacionAndFechaExpiracionAfter(
                                idPublicacion,
                                LocalDateTime.now());

        LocalDate finBloqueadoNuevo = fechaFin.plusDays(1);

        for (Carrito carritoExistente : carritosVigentes) {

            if (idCarritoIgnorado != null
                    && carritoExistente.getIdCarrito()
                            .equals(idCarritoIgnorado)) {
                continue;
            }

            LocalDate finBloqueadoExistente =
                    carritoExistente.getFechaFin().plusDays(1);

            boolean haySolapamiento =
                    fechaInicio.isBefore(finBloqueadoExistente)
                    && carritoExistente.getFechaInicio()
                            .isBefore(finBloqueadoNuevo);

            if (haySolapamiento) {
                throw new CarritoInvalidException();
            }
        }
    }

    private void validarReservasBloqueantes(
            Long idVehiculo,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws CarritoInvalidException {

        try {
            reservaService.validarSolapamientoPorVehiculo(
                    idVehiculo,
                    fechaInicio,
                    fechaFin);
        } catch (ReservaInvalidException exception) {
            throw new CarritoInvalidException();
        }
    }

    private void validarRequest(
            Long idPublicacion,
            AgregarCarritoRequest request)
            throws CarritoInvalidException {

        if (request == null) {
            throw new CarritoInvalidException();
        }

        if (idPublicacion == null) {
            throw new CarritoInvalidException();
        }

        validarFechas(
                request.getFechaInicio(),
                request.getFechaFin());
    }

    private CarritoResponse convertirAResponse(Carrito carrito) {

        long cantidadDias = ChronoUnit.DAYS.between(
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        BigDecimal precioTotal =
                carrito.getPrecioDiaAplicado()
                        .multiply(BigDecimal.valueOf(cantidadDias))
                        .setScale(2, RoundingMode.HALF_UP);

        return new CarritoResponse(
                carrito.getIdCarrito(),
                carrito.getPublicacion().getIdPublicacion(),
                carrito.getFechaInicio(),
                carrito.getFechaFin(),
                carrito.getFechaExpiracion(),
                carrito.getPrecioDiaAplicado(),
                cantidadDias,
                precioTotal);
    }

    private BigDecimal calcularPrecioDiaConDescuento(
            Publicacion publicacion) {

        BigDecimal porcentaje =
                publicacion.getDescuentoPorcentaje();

        if (porcentaje == null) {
            porcentaje = BigDecimal.ZERO;
        }

        BigDecimal porcentajeDecimal =
                porcentaje.movePointLeft(2);

        return publicacion.getPrecioDia()
                .multiply(
                        BigDecimal.ONE.subtract(
                                porcentajeDecimal))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
