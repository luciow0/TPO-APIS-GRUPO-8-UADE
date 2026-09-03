package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.uade.tpo.marketplace.dto.PublicacionRequest;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.exception.PublicacionDuplicateException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.repository.PublicacionRepository;

@Service
public class PublicacionServiceImpl implements PublicacionService {

        @Autowired
        private PublicacionRepository publicacionRepository;

        @Autowired
        private VehiculoService vehiculoService;

        @Autowired
        private UbicacionService ubicacionService;

        @Override
        public Publicacion crearPublicacion(PublicacionRequest request)
                        throws PublicacionDuplicateException {

                validarRequest(request);

                Vehiculo vehiculo = vehiculoService
                                .buscarPorId(request.getIdVehiculo())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Vehiculo no encontrado"));

                Ubicacion ubicacion = ubicacionService
                                .obtenerUbicacionPorId(request.getIdUbicacion())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Ubicacion no encontrada"));

                if (publicacionRepository
                                .existsByVehiculo_IdVehiculo(request.getIdVehiculo())) {

                        throw new PublicacionDuplicateException();
                }

                BigDecimal descuento = request.getDescuentoPorcentaje();

                if (descuento == null) {
                        descuento = BigDecimal.ZERO;
                }

                Publicacion publicacion = new Publicacion(
                                request.getPrecioDia(),
                                descuento,
                                request.getDescripcion(),
                                request.getHoraRetiroDevolucion(),
                                vehiculo,
                                ubicacion);

                return publicacionRepository.save(publicacion);
        }

        @Override
        public List<Publicacion> obtenerPublicaciones() {
                return publicacionRepository.findAll();
        }

        @Override
        public Publicacion obtenerPublicacionPorId(Long id)
                        throws PublicacionNotFoundException {

                return publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);
        }

        @Override
        public Publicacion modificarPublicacion(
                        Long id,
                        PublicacionRequest request)
                        throws PublicacionNotFoundException,
                        PublicacionDuplicateException {

                Publicacion publicacion = publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);

                validarRequest(request);

                Vehiculo vehiculo = vehiculoService
                                .buscarPorId(request.getIdVehiculo())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Vehiculo no encontrado"));

                Ubicacion ubicacion = ubicacionService
                                .obtenerUbicacionPorId(request.getIdUbicacion())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Ubicacion no encontrada"));

                if (!publicacion.getVehiculo()
                                .getIdVehiculo()
                                .equals(request.getIdVehiculo())
                                && publicacionRepository
                                                .existsByVehiculo_IdVehiculo(
                                                                request.getIdVehiculo())) {

                        throw new PublicacionDuplicateException();
                }

                BigDecimal descuento = request.getDescuentoPorcentaje();

                if (descuento == null) {
                        descuento = BigDecimal.ZERO;
                }

                publicacion.setPrecioDia(request.getPrecioDia());
                publicacion.setDescuentoPorcentaje(descuento);
                publicacion.setDescripcion(request.getDescripcion());
                publicacion.setHoraRetiroDevolucion(
                                request.getHoraRetiroDevolucion());
                publicacion.setVehiculo(vehiculo);
                publicacion.setUbicacion(ubicacion);

                return publicacionRepository.save(publicacion);
        }

        @Override
        public void eliminarPublicacion(Long id)
                        throws PublicacionNotFoundException {

                Publicacion publicacion = publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);

                publicacion.setEstado(EstadoPublicacion.DESACTIVADA);

                publicacionRepository.save(publicacion);
        }

        @Override
        public Publicacion pausarPublicacion(Long id)
                        throws PublicacionNotFoundException {

                Publicacion publicacion = publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);

                if (publicacion.getEstado() == EstadoPublicacion.DESACTIVADA) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "No se puede pausar una publicacion desactivada");
                }

                publicacion.setEstado(EstadoPublicacion.PAUSADA);

                return publicacionRepository.save(publicacion);
        }

        @Override
        public Publicacion reactivarPublicacion(Long id)
                        throws PublicacionNotFoundException {

                Publicacion publicacion = publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);

                publicacion.setEstado(EstadoPublicacion.ACTIVA);

                return publicacionRepository.save(publicacion);
        }

        @Override
        public List<Publicacion> obtenerPublicacionesPorEstado(
                        EstadoPublicacion estado) {

                return publicacionRepository.findByEstado(estado);
        }

        @Override
        public List<Publicacion> obtenerPublicacionesPorPrecio(
                        BigDecimal precioMin,
                        BigDecimal precioMax) {

                if (precioMin == null || precioMax == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Los precios minimo y maximo son obligatorios");
                }

                if (precioMin.compareTo(BigDecimal.ZERO) < 0
                                || precioMax.compareTo(BigDecimal.ZERO) < 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Los precios no pueden ser negativos");
                }

                if (precioMin.compareTo(precioMax) > 0) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El precio minimo no puede ser mayor al precio maximo");
                }

                return soloPublicacionesActivas(
                                publicacionRepository.findByPrecioDiaBetween(
                                                precioMin,
                                                precioMax));
        }

        @Override
        public List<Publicacion> obtenerPublicacionesPorTipoVehiculo(
                        Long idTipoVehiculo) {

                if (idTipoVehiculo == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El tipo de vehiculo es obligatorio");
                }

                return soloPublicacionesActivas(
                                publicacionRepository
                                                .findByVehiculo_TipoVehiculo_IdTipoVehiculo(
                                                                idTipoVehiculo));
        }

        @Override
        public List<Publicacion> obtenerPublicacionesPorMarca(
                        String marca) {

                validarTextoFiltro(marca, "La marca es obligatoria");

                return soloPublicacionesActivas(
                                publicacionRepository
                                                .findByVehiculo_MarcaIgnoreCase(
                                                                marca.trim()));
        }

        @Override
        public List<Publicacion> obtenerPublicacionesPorModelo(
                        String modelo) {

                validarTextoFiltro(modelo, "El modelo es obligatorio");

                return soloPublicacionesActivas(
                                publicacionRepository
                                                .findByVehiculo_ModeloIgnoreCase(
                                                                modelo.trim()));
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorProvincia(
                        String provincia,
                        Pageable pageable) {

                validarTextoFiltro(
                                provincia,
                                "La provincia es obligatoria");

                return publicacionRepository
                                .findByUbicacion_ProvinciaIgnoreCaseAndEstado(
                                                provincia.trim(),
                                                EstadoPublicacion.ACTIVA,
                                                pageable);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorProvinciaYCiudad(
                        String provincia,
                        String ciudad,
                        Pageable pageable) {

                validarTextoFiltro(
                                provincia,
                                "La provincia es obligatoria");

                validarTextoFiltro(
                                ciudad,
                                "La ciudad es obligatoria");

                return publicacionRepository
                                .findByUbicacion_ProvinciaIgnoreCaseAndUbicacion_CiudadIgnoreCaseAndEstado(
                                                provincia.trim(),
                                                ciudad.trim(),
                                                EstadoPublicacion.ACTIVA,
                                                pageable);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorProvinciaCiudadYLocalidad(
                        String provincia,
                        String ciudad,
                        String localidad,
                        Pageable pageable) {

                validarTextoFiltro(
                                provincia,
                                "La provincia es obligatoria");

                validarTextoFiltro(
                                ciudad,
                                "La ciudad es obligatoria");

                validarTextoFiltro(
                                localidad,
                                "La localidad es obligatoria");

                return publicacionRepository
                                .findByUbicacion_ProvinciaIgnoreCaseAndUbicacion_CiudadIgnoreCaseAndUbicacion_LocalidadIgnoreCaseAndEstado(
                                                provincia.trim(),
                                                ciudad.trim(),
                                                localidad.trim(),
                                                EstadoPublicacion.ACTIVA,
                                                pageable);
        }

        private List<Publicacion> soloPublicacionesActivas(
                        List<Publicacion> publicaciones) {

                return publicaciones.stream()
                                .filter(publicacion -> publicacion.getEstado() == EstadoPublicacion.ACTIVA)
                                .toList();
        }

        private void validarTextoFiltro(
                        String valor,
                        String mensaje) {

                if (valor == null || valor.isBlank()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        mensaje);
                }
        }

        private void validarRequest(PublicacionRequest request) {

                if (request == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "La publicacion no puede estar vacia");
                }

                if (request.getPrecioDia() == null
                                || request.getPrecioDia()
                                                .compareTo(BigDecimal.ZERO) <= 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El precio por dia debe ser mayor a cero");
                }

                if (request.getDescuentoPorcentaje() != null
                                && (request.getDescuentoPorcentaje()
                                                .compareTo(BigDecimal.ZERO) < 0
                                                || request.getDescuentoPorcentaje()
                                                                .compareTo(
                                                                                new BigDecimal("100")) > 0)) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El descuento debe estar entre 0 y 100");
                }

                if (request.getDescripcion() == null
                                || request.getDescripcion().isBlank()) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "La descripcion es obligatoria");
                }

                if (request.getHoraRetiroDevolucion() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "La hora de retiro y devolucion es obligatoria");
                }

                if (request.getIdVehiculo() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El vehiculo es obligatorio");
                }

                if (request.getIdUbicacion() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "La ubicacion es obligatoria");
                }
        }
}
