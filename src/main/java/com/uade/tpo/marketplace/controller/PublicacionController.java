package com.uade.tpo.marketplace.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.auth.SeguridadDominio;
import com.uade.tpo.marketplace.dto.PublicacionRequest;
import com.uade.tpo.marketplace.dto.PublicacionResponse;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.entity.TipoVehiculo;
import com.uade.tpo.marketplace.entity.Ubicacion;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.exception.PublicacionDuplicateException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;
import com.uade.tpo.marketplace.service.PublicacionService;

@RestController
@RequestMapping("publicaciones")
public class PublicacionController {

    private static final BigDecimal PASO_CUADRICULA = new BigDecimal("0.005");

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private SeguridadDominio seguridadDominio;

    @GetMapping
    public ResponseEntity<Page<PublicacionResponse>> obtenerPublicaciones(Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService.obtenerPublicaciones(pageable)
                        .map(this::convertirAResponse));
    }

    // Catálogo con filtros combinables y paginación. Las fechas van como yyyy-MM-dd.
    @GetMapping("/buscar")
    public ResponseEntity<Page<PublicacionResponse>> buscarCatalogo(
            @RequestParam(required = false) String zona,
            @RequestParam(required = false) Long idTipoVehiculo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String orden,
            @PageableDefault(size = 9) Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService.buscarCatalogo(zona, idTipoVehiculo, fechaInicio, fechaFin, orden, pageable)
                        .map(this::convertirAResponse));
    }

    // Zonas con publicaciones activas, para el selector del catálogo.
    @GetMapping("/zonas")
    public ResponseEntity<List<String>> obtenerZonasDelCatalogo() {
        return ResponseEntity.ok(publicacionService.obtenerZonasDelCatalogo());
    }

    @GetMapping("/mias")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PublicacionResponse>> obtenerMisPublicaciones(
            Authentication authentication,
            @RequestParam(required = false) EstadoPublicacion estado,
            @RequestParam(required = false) String busqueda,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService.obtenerMisPublicaciones(authentication.getName(), estado, busqueda, pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@seguridadDominio.puedeVerPublicacion(authentication, #id)")
    public ResponseEntity<PublicacionResponse> obtenerPublicacionPorId(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        Publicacion publicacion =
                publicacionService.obtenerPublicacionPorId(id);

        return ResponseEntity.ok(convertirAResponse(publicacion));
    }

    @PostMapping
    public ResponseEntity<PublicacionResponse> crearPublicacion(
            @RequestBody PublicacionRequest request)
            throws PublicacionDuplicateException {

        Publicacion publicacion =
                publicacionService.crearPublicacion(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/publicaciones/"
                                        + publicacion.getIdPublicacion()))
                .body(convertirAResponse(publicacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublicacionResponse> modificarPublicacion(
            @PathVariable Long id,
            @RequestBody PublicacionRequest request)
            throws PublicacionNotFoundException {

        Publicacion publicacion =
                publicacionService.modificarPublicacion(
                        id,
                        request);

        return ResponseEntity.ok(convertirAResponse(publicacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        publicacionService.eliminarPublicacion(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/pausar")
    public ResponseEntity<PublicacionResponse> pausarPublicacion(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        Publicacion publicacion =
                publicacionService.pausarPublicacion(id);

        return ResponseEntity.ok(convertirAResponse(publicacion));
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<PublicacionResponse> reactivarPublicacion(
            @PathVariable Long id)
            throws PublicacionNotFoundException {

        Publicacion publicacion =
                publicacionService.reactivarPublicacion(id);

        return ResponseEntity.ok(convertirAResponse(publicacion));
    }

    @GetMapping("/filtros/estado")
    @PreAuthorize("#estado == T(com.uade.tpo.marketplace.Enum.EstadoPublicacion).ACTIVA or hasRole('ADMIN')")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorEstado(
                    @RequestParam EstadoPublicacion estado,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorEstado(estado, pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/precio")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorPrecio(
                    @RequestParam BigDecimal precioMin,
                    @RequestParam BigDecimal precioMax,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorPrecio(
                                precioMin,
                                precioMax,
                                pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/tipo")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorTipoVehiculo(
                    @RequestParam Long idTipoVehiculo,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorTipoVehiculo(
                                idTipoVehiculo,
                                pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/marca")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorMarca(
                    @RequestParam String marca,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorMarca(marca, pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/modelo")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorModelo(
                    @RequestParam String modelo,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorModelo(modelo, pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/provincia")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorProvincia(
                    @RequestParam String provincia,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorProvincia(
                                provincia,
                                pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/provincia-ciudad")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorProvinciaYCiudad(
                    @RequestParam String provincia,
                    @RequestParam String ciudad,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorProvinciaYCiudad(
                                provincia,
                                ciudad,
                                pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/provincia-ciudad-localidad")
    public ResponseEntity<Page<PublicacionResponse>>
            obtenerPublicacionesPorProvinciaCiudadYLocalidad(
                    @RequestParam String provincia,
                    @RequestParam String ciudad,
                    @RequestParam String localidad,
                    Pageable pageable) {

        return ResponseEntity.ok(
                publicacionService
                        .obtenerPublicacionesPorProvinciaCiudadYLocalidad(
                                provincia,
                                ciudad,
                                localidad,
                                pageable)
                        .map(this::convertirAResponse));
    }

    @GetMapping("/filtros/zona")
    public ResponseEntity<Page<PublicacionResponse>> obtenerPublicacionesPorZona(
            @RequestParam String zona, Pageable pageable) {
        return ResponseEntity.ok(
                publicacionService.obtenerPublicacionesPorZona(zona, pageable)
                        .map(this::convertirAResponse));
    }

    private PublicacionResponse convertirAResponse(Publicacion publicacion) {
        Vehiculo vehiculo = publicacion.getVehiculo();
        TipoVehiculo tipoVehiculo = vehiculo.getTipoVehiculo();
        Ubicacion ubicacion = publicacion.getUbicacion();

        boolean exacta = seguridadDominio.puedeVerUbicacionExacta(
                SecurityContextHolder.getContext().getAuthentication(), publicacion);

        return new PublicacionResponse(
                publicacion.getIdPublicacion(),
                publicacion.getFechaPublicacion(),
                publicacion.getEstado(),
                publicacion.getPrecioDia(),
                publicacion.getDescuentoPorcentaje(),
                publicacion.getDescripcion(),
                publicacion.getHoraRetiroDevolucion(),
                vehiculo.getIdVehiculo(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getColor(),
                vehiculo.getCantidadAsientos(),
                tipoVehiculo != null ? tipoVehiculo.getIdTipoVehiculo() : null,
                tipoVehiculo != null ? tipoVehiculo.getNombre() : null,
                ubicacion.getIdUbicacion(),
                ubicacion.getProvincia(),
                ubicacion.getCiudad(),
                ubicacion.getLocalidad(),
                ubicacion.getZona(),
                exacta ? ubicacion.getDireccion() : null,
                exacta ? ubicacion.getLatitud() : zonaAproximada(ubicacion.getLatitud()),
                exacta ? ubicacion.getLongitud() : zonaAproximada(ubicacion.getLongitud()));
    }

    // Lleva la coordenada al centro de su cuadro de la cuadrícula (~500 m).
    // Misma cuenta que el front: Math.round(x / PASO) * PASO, es decir floor(x / PASO + 0.5).
    private static BigDecimal zonaAproximada(BigDecimal coordenada) {
        if (coordenada == null) {
            return null;
        }
        return coordenada.divide(PASO_CUADRICULA)
                .add(new BigDecimal("0.5"))
                .setScale(0, RoundingMode.FLOOR)
                .multiply(PASO_CUADRICULA);
    }

}
