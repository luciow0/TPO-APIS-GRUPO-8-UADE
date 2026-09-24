package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.marketplace.dto.PublicacionRequest;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.Enum.EstadoPublicacion;
import com.uade.tpo.marketplace.exception.PublicacionDuplicateException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;

public interface PublicacionService {

        Publicacion crearPublicacion(PublicacionRequest request)
                        throws PublicacionDuplicateException;

        Page<Publicacion> obtenerPublicaciones(Pageable pageable);

        Page<Publicacion> obtenerMisPublicaciones(String emailUsuario, Pageable pageable);

        Publicacion obtenerPublicacionPorId(Long id)
                        throws PublicacionNotFoundException;

        Publicacion modificarPublicacion(
                        Long id,
                        PublicacionRequest request)
                        throws PublicacionNotFoundException,
                        PublicacionDuplicateException;

        void eliminarPublicacion(Long id)
                        throws PublicacionNotFoundException;

        Publicacion pausarPublicacion(Long id)
                        throws PublicacionNotFoundException;

        Publicacion reactivarPublicacion(Long id)
                        throws PublicacionNotFoundException;

        Page<Publicacion> obtenerPublicacionesPorEstado(
                        EstadoPublicacion estado,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorPrecio(
                        BigDecimal precioMin,
                        BigDecimal precioMax,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorTipoVehiculo(
                        Long idTipoVehiculo,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorMarca(
                        String marca,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorModelo(
                        String modelo,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorProvincia(
                        String provincia,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorProvinciaYCiudad(
                        String provincia,
                        String ciudad,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorProvinciaCiudadYLocalidad(
                        String provincia,
                        String ciudad,
                        String localidad,
                        Pageable pageable);

        Page<Publicacion> obtenerPublicacionesPorZona(String zona, Pageable pageable);
}
