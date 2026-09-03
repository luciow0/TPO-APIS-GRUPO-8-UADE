package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.dto.CarritoRequest;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.Disponibilidad;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.entity.Reserva;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exception.CarritoDuplicateException;
import com.uade.tpo.marketplace.exception.CarritoInvalidException;
import com.uade.tpo.marketplace.exception.CarritoNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
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

    @Override
    public Carrito crearCarrito(CarritoRequest request)
            throws CarritoInvalidException,
            CarritoDuplicateException,
            PublicacionNotFoundException,
            UsuarioNotFoundException {

        validarRequest(request);

        Usuario usuario =
                usuarioService.obtenerUsuarioPorId(
                        request.getIdUsuario());

        validarCarritoDelUsuario(request.getIdUsuario());

        Publicacion publicacion =
                publicacionService.obtenerPublicacionPorId(
                        request.getIdPublicacion());

        validarPublicacionActiva(publicacion);

        validarDisponibilidad(
                publicacion,
                request.getFechaInicio(),
                request.getFechaFin());

        validarReservasBloqueantes(
                publicacion.getIdPublicacion(),
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
        carrito.setPrecioDiaAplicado(publicacion.getPrecioDia());

        return carritoRepository.save(carrito);
    }

    @Override
    public Optional<Carrito> obtenerCarritoPorUsuario(Long idUsuario) {
        Optional<Carrito> carritoOptional =
                carritoRepository.findByUsuario_IdUsuario(idUsuario);

        if (carritoOptional.isEmpty()) {
            return Optional.empty();
        }

        Carrito carrito = carritoOptional.get();

        if (!carrito.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            return Optional.empty();
        }

        return Optional.of(carrito);
    }

    @Override
    public Carrito modificarFechas(
            Long idCarrito,
            Long idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException {

        Optional<Carrito> carritoOptional =
                carritoRepository.findById(idCarrito);

        if (carritoOptional.isEmpty()) {
            throw new CarritoNotFoundException();
        }

        Carrito carrito = carritoOptional.get();

        if (!carrito.getUsuario()
                .getIdUsuario()
                .equals(idUsuario)) {

            throw new CarritoInvalidException();
        }

        if (!carrito.getFechaExpiracion()
                .isAfter(LocalDateTime.now())) {

            carritoRepository.delete(carrito);
            throw new CarritoNotFoundException();
        }

        validarFechas(fechaInicio, fechaFin);

        Publicacion publicacion = carrito.getPublicacion();

        validarPublicacionActiva(publicacion);

        validarDisponibilidad(
                publicacion,
                fechaInicio,
                fechaFin);

        validarReservasBloqueantes(
                publicacion.getIdPublicacion(),
                fechaInicio,
                fechaFin);

        validarSolapamientoCarritos(
                publicacion.getIdPublicacion(),
                fechaInicio,
                fechaFin,
                idCarrito);

        carrito.setFechaInicio(fechaInicio);
        carrito.setFechaFin(fechaFin);

        return carritoRepository.save(carrito);
    }

    @Override
    public void eliminarCarrito(Long idCarrito, Long idUsuario)
            throws CarritoNotFoundException,
            CarritoInvalidException {

        Optional<Carrito> carritoOptional =
                carritoRepository.findById(idCarrito);

        if (carritoOptional.isEmpty()) {
            throw new CarritoNotFoundException();
        }

        Carrito carrito = carritoOptional.get();

        if (!carrito.getUsuario()
                .getIdUsuario()
                .equals(idUsuario)) {

            throw new CarritoInvalidException();
        }

        carritoRepository.delete(carrito);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reserva continuarReserva(Long idCarrito, Long idUsuario)
            throws CarritoNotFoundException,
            CarritoInvalidException,
            PublicacionNotFoundException,
            ReservaInvalidException {

        Optional<Carrito> carritoOptional =
                carritoRepository.findById(idCarrito);

        if (carritoOptional.isEmpty()) {
            throw new CarritoNotFoundException();
        }

        Carrito carrito = carritoOptional.get();

        if (!carrito.getUsuario()
                .getIdUsuario()
                .equals(idUsuario)) {

            throw new CarritoInvalidException();
        }

        if (!carrito.getFechaExpiracion()
                .isAfter(LocalDateTime.now())) {

            throw new CarritoNotFoundException();
        }

        validarFechas(
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        Publicacion publicacion = carrito.getPublicacion();

        validarPublicacionActiva(publicacion);

        validarDisponibilidad(
                publicacion,
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        validarReservasBloqueantes(
                publicacion.getIdPublicacion(),
                carrito.getFechaInicio(),
                carrito.getFechaFin());

        validarSolapamientoCarritos(
                publicacion.getIdPublicacion(),
                carrito.getFechaInicio(),
                carrito.getFechaFin(),
                carrito.getIdCarrito());

        Reserva reserva =
                reservaService.crearReservaDesdeCarrito(carrito);

        carritoRepository.delete(carrito);

        return reserva;
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
            throws CarritoInvalidException {

        if (publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
            throw new CarritoInvalidException();
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

        if (carritoExistente.getFechaExpiracion()
                .isAfter(LocalDateTime.now())) {

            throw new CarritoDuplicateException();
        }

        carritoRepository.delete(carritoExistente);
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
            Long idPublicacion,
            LocalDate fechaInicio,
            LocalDate fechaFin)
            throws CarritoInvalidException {

        try {
            reservaService.validarSolapamiento(
                    idPublicacion,
                    fechaInicio,
                    fechaFin);
        } catch (ReservaInvalidException exception) {
            throw new CarritoInvalidException();
        }
    }

    private void validarRequest(CarritoRequest request)
            throws CarritoInvalidException {

        if (request == null) {
            throw new CarritoInvalidException();
        }

        if (request.getIdUsuario() == null) {
            throw new CarritoInvalidException();
        }

        if (request.getIdPublicacion() == null) {
            throw new CarritoInvalidException();
        }

        validarFechas(
                request.getFechaInicio(),
                request.getFechaFin());
    }
}
