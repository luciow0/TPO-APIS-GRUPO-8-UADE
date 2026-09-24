package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

        @PreAuthorize("@seguridadDominio.esDuenioDeVehiculo(authentication, #request.idVehiculo) or hasRole('ADMIN')")
        @Override
        public Publicacion crearPublicacion(PublicacionRequest request)
                        throws PublicacionDuplicateException {

                validarRequest(request);

                Vehiculo vehiculo = vehiculoService
                                .obtenerVehiculoPorId(request.getIdVehiculo())
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
        public Page<Publicacion> obtenerPublicaciones(Pageable pageable) {
                return publicacionRepository.findByEstado(EstadoPublicacion.ACTIVA, pageable);
        }

        @PreAuthorize("isAuthenticated()")
        @Override
        public Page<Publicacion> obtenerMisPublicaciones(String emailUsuario, Pageable pageable) {
                return publicacionRepository.findByVehiculo_Propietario_Email(emailUsuario, pageable);
        }

        @Override
        public Publicacion obtenerPublicacionPorId(Long id)
                        throws PublicacionNotFoundException {

                return publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);
        }

        @PreAuthorize(
        "(@seguridadDominio.esDuenioDePublicacion(authentication, #id) " +
        "and @seguridadDominio.esDuenioDeVehiculo(authentication, #request.idVehiculo)) " +
        "or hasRole('ADMIN')"
)
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
                                .obtenerVehiculoPorId(request.getIdVehiculo())
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

        @PreAuthorize("@seguridadDominio.esDuenioDePublicacion(authentication, #id) or hasRole('ADMIN')")
        @Override
        public void eliminarPublicacion(Long id)
                        throws PublicacionNotFoundException {

                Publicacion publicacion = publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);

                publicacion.setEstado(EstadoPublicacion.DESACTIVADA);

                publicacionRepository.save(publicacion);
        }

        @PreAuthorize("@seguridadDominio.esDuenioDePublicacion(authentication, #id) or hasRole('ADMIN')")
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

        @PreAuthorize("@seguridadDominio.esDuenioDePublicacion(authentication, #id) or hasRole('ADMIN')")
        @Override
        public Publicacion reactivarPublicacion(Long id)
                        throws PublicacionNotFoundException {

                Publicacion publicacion = publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);

                publicacion.setEstado(EstadoPublicacion.ACTIVA);

                return publicacionRepository.save(publicacion);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorEstado(
                        EstadoPublicacion estado,
                        Pageable pageable) {

                return publicacionRepository.findByEstado(estado, pageable);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorPrecio(
                        BigDecimal precioMin,
                        BigDecimal precioMax,
                        Pageable pageable) {

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

                return publicacionRepository.findByPrecioDiaBetweenAndEstado(
                                precioMin,
                                precioMax,
                                EstadoPublicacion.ACTIVA,
                                pageable);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorTipoVehiculo(
                        Long idTipoVehiculo,
                        Pageable pageable) {

                if (idTipoVehiculo == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El tipo de vehiculo es obligatorio");
                }

                return publicacionRepository
                                .findByVehiculo_TipoVehiculo_IdTipoVehiculoAndEstado(
                                                idTipoVehiculo,
                                                EstadoPublicacion.ACTIVA,
                                                pageable);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorMarca(
                        String marca,
                        Pageable pageable) {

                validarTextoFiltro(marca, "La marca es obligatoria");

                return publicacionRepository
                                .findByVehiculo_MarcaIgnoreCaseAndEstado(
                                                marca.trim(), EstadoPublicacion.ACTIVA, pageable);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorModelo(
                        String modelo,
                        Pageable pageable) {

                validarTextoFiltro(modelo, "El modelo es obligatorio");

                return publicacionRepository
                                .findByVehiculo_ModeloIgnoreCaseAndEstado(
                                                modelo.trim(), EstadoPublicacion.ACTIVA, pageable);
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
                                                                                new BigDecimal("90")) > 0)) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El descuento debe estar entre 0 y 90");
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

        @Override
        public Page<Publicacion> obtenerPublicacionesPorZona(String zona, Pageable pageable) {
                validarTextoFiltro(zona, "La zona es obligatoria");

                return publicacionRepository
                                .findByUbicacion_ZonaIgnoreCaseAndEstado(
                                                zona.trim(), EstadoPublicacion.ACTIVA, pageable);
        }

}
