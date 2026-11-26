package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uade.tpo.marketplace.dto.PublicacionRequest;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.Enum.EstadoReserva;
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
                                                "No encontramos ese vehículo."));

                Ubicacion ubicacion = ubicacionService
                                .obtenerUbicacionPorId(request.getIdUbicacion())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "No encontramos esa ubicación."));

                validarUbicacionPropia(ubicacion, vehiculo);

                if (publicacionRepository
                                .existsByVehiculo_IdVehiculoAndEstadoIn(
                                                request.getIdVehiculo(),
                                                List.of(
                                                                EstadoPublicacion.ACTIVA,
                                                                EstadoPublicacion.PAUSADA))) {

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

        @PreAuthorize("isAuthenticated() and #emailUsuario == authentication.name")
        @Override
        @Transactional(readOnly = true)
        public Page<Publicacion> obtenerMisPublicaciones(
                        String emailUsuario, EstadoPublicacion estado, String busqueda, Pageable pageable) {
                String patron = null;
                if (busqueda != null && !busqueda.isBlank()) {
                        String texto = busqueda.trim().toLowerCase(Locale.ROOT)
                                        .replace("!", "!!").replace("%", "!%").replace("_", "!_");
                        patron = "%" + texto + "%";
                }
                Pageable pagina = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100));
                if (pagina.getOffset() > Integer.MAX_VALUE) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay más resultados para mostrar.");
                }
                return publicacionRepository.buscarMisPublicaciones(emailUsuario, estado, patron, pagina);
        }

        @Override
        public Publicacion obtenerPublicacionPorId(Long id)
                        throws PublicacionNotFoundException {

                return publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);
        }

        @PreAuthorize("@seguridadDominio.esDuenioDePublicacion(authentication, #id) or hasRole('ADMIN')")
        @Override
        public Publicacion modificarPublicacion(
                        Long id,
                        PublicacionRequest request)
                        throws PublicacionNotFoundException {

                Publicacion publicacion = publicacionRepository.findById(id)
                                .orElseThrow(PublicacionNotFoundException::new);

                validarRequest(request);

                if (!publicacion.getVehiculo()
                                .getIdVehiculo()
                                .equals(request.getIdVehiculo())) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "No podés cambiar el vehículo de una publicación.");
                }

                Ubicacion ubicacion = ubicacionService
                                .obtenerUbicacionPorId(request.getIdUbicacion())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "No encontramos esa ubicación."));

                validarUbicacionPropia(ubicacion, publicacion.getVehiculo());

                BigDecimal descuento = request.getDescuentoPorcentaje();

                if (descuento == null) {
                        descuento = BigDecimal.ZERO;
                }

                publicacion.setPrecioDia(request.getPrecioDia());
                publicacion.setDescuentoPorcentaje(descuento);
                publicacion.setDescripcion(request.getDescripcion());
                publicacion.setHoraRetiroDevolucion(
                                request.getHoraRetiroDevolucion());
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
                                        "No podés pausar una publicación dada de baja.");
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

                if (publicacion.getEstado() == EstadoPublicacion.DESACTIVADA) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "No podés reactivar una publicación dada de baja.");
                }

                if (publicacion.getEstado() != EstadoPublicacion.PAUSADA) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Solo podés reactivar una publicación pausada.");
                }

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
                                        "Ingresá el precio mínimo y el máximo.");
                }

                if (precioMin.compareTo(BigDecimal.ZERO) < 0
                                || precioMax.compareTo(BigDecimal.ZERO) < 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Los precios no pueden ser negativos.");
                }

                if (precioMin.compareTo(precioMax) > 0) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El precio mínimo no puede ser mayor al máximo.");
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
                                        "Elegí un tipo de vehículo.");
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

                validarTextoFiltro(marca, "Ingresá la marca.");

                return publicacionRepository
                                .findByVehiculo_MarcaIgnoreCaseAndEstado(
                                                marca.trim(), EstadoPublicacion.ACTIVA, pageable);
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorModelo(
                        String modelo,
                        Pageable pageable) {

                validarTextoFiltro(modelo, "Ingresá el modelo.");

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
                                "Elegí la provincia.");

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
                                "Elegí la provincia.");

                validarTextoFiltro(
                                ciudad,
                                "Elegí la ciudad.");

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
                                "Elegí la provincia.");

                validarTextoFiltro(
                                ciudad,
                                "Elegí la ciudad.");

                validarTextoFiltro(
                                localidad,
                                "Elegí la localidad.");

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

        // Una ubicación ya usada por una publicación de otro propietario no se puede reutilizar
        private void validarUbicacionPropia(Ubicacion ubicacion, Vehiculo vehiculo) {
                String emailPropietario = vehiculo.getPropietario().getEmail();

                boolean usadaPorOtro = publicacionRepository
                                .findByUbicacion_IdUbicacion(ubicacion.getIdUbicacion())
                                .stream()
                                .anyMatch(p -> !p.getVehiculo().getPropietario().getEmail()
                                                .equals(emailPropietario));

                if (usadaPorOtro) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Esa ubicación no es tuya. Elegí una de tus ubicaciones o cargá una nueva.");
                }
        }

        private void validarRequest(PublicacionRequest request) {

                ValidacionVolanta.validarDatosCompletos(request);
                ValidacionVolanta.validarPrecioDiaPositivo(request.getPrecioDia());
                ValidacionVolanta.validarDescuentoValido(request.getDescuentoPorcentaje());
                ValidacionVolanta.validarDescripcionNoVacia(request.getDescripcion());
                ValidacionVolanta.validarHoraRetiroObligatoria(request.getHoraRetiroDevolucion());
                ValidacionVolanta.validarIdVehiculoPositivo(request.getIdVehiculo());
                ValidacionVolanta.validarIdUbicacionPositivo(request.getIdUbicacion());
        }

        @Override
        public Page<Publicacion> obtenerPublicacionesPorZona(String zona, Pageable pageable) {
                validarTextoFiltro(zona, "Elegí la zona.");

                return publicacionRepository
                                .findByUbicacion_ZonaIgnoreCaseAndEstado(
                                                zona.trim(), EstadoPublicacion.ACTIVA, pageable);
        }

        private static final List<String> ORDENES_CATALOGO =
                        List.of("recientes", "precio-asc", "precio-desc", "anio-desc");

        @Override
        @Transactional(readOnly = true)
        public Page<Publicacion> buscarCatalogo(
                        String zona,
                        Long idTipoVehiculo,
                        LocalDate fechaInicio,
                        LocalDate fechaFin,
                        String orden,
                        Pageable pageable) {

                if ((fechaInicio == null) != (fechaFin == null)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Elegí la fecha de retiro y la de devolución.");
                }
                if (fechaInicio != null && fechaInicio.isBefore(LocalDate.now())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "La fecha de retiro no puede ser anterior a hoy.");
                }
                if (fechaInicio != null && !fechaFin.isAfter(fechaInicio)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "La devolución tiene que ser al menos un día después del retiro.");
                }
                String ordenElegido = orden == null || orden.isBlank() ? "recientes" : orden;
                if (!ORDENES_CATALOGO.contains(ordenElegido)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elegí un orden de la lista.");
                }

                // El orden lo arma la consulta; de la página solo se usan número y tamaño
                Pageable pagina = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100));
                if (pagina.getOffset() > Integer.MAX_VALUE) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay más resultados para mostrar.");
                }

                return publicacionRepository.buscarCatalogo(
                                EstadoPublicacion.ACTIVA,
                                zona == null || zona.isBlank() ? null : zona.trim().toLowerCase(Locale.ROOT),
                                idTipoVehiculo,
                                fechaInicio,
                                fechaFin,
                                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                                LocalDateTime.now(),
                                ordenElegido,
                                pagina);
        }

        @Override
        public List<String> obtenerZonasDelCatalogo() {
                return publicacionRepository.buscarZonas(EstadoPublicacion.ACTIVA);
        }

}
