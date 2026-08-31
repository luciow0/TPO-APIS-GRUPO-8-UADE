package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;
import java.util.List;

import com.uade.tpo.marketplace.dto.PublicacionRequest;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.enums.EstadoPublicacion;
import com.uade.tpo.marketplace.exceptions.PublicacionDuplicateException;
import com.uade.tpo.marketplace.exceptions.PublicacionNotFoundException;

public interface PublicacionService {

    Publicacion crearPublicacion(PublicacionRequest request)
            throws PublicacionDuplicateException;

    List<Publicacion> obtenerPublicaciones();

    Publicacion obtenerPublicacionPorId(Long id)
            throws PublicacionNotFoundException;

    Publicacion modificarPublicacion(Long id, PublicacionRequest request)
            throws PublicacionNotFoundException, PublicacionDuplicateException;

    void eliminarPublicacion(Long id)
            throws PublicacionNotFoundException;

    Publicacion pausarPublicacion(Long id)
            throws PublicacionNotFoundException;

    Publicacion reactivarPublicacion(Long id)
            throws PublicacionNotFoundException;

    List<Publicacion> obtenerPublicacionesPorEstado(
            EstadoPublicacion estado);

    List<Publicacion> obtenerPublicacionesPorPrecio(
            BigDecimal precioMin,
            BigDecimal precioMax);

    List<Publicacion> obtenerPublicacionesPorTipoVehiculo(
            Long idTipoVehiculo);

    List<Publicacion> obtenerPublicacionesPorMarca(
            String marca);

    List<Publicacion> obtenerPublicacionesPorModelo(
            String modelo);

    List<Publicacion> obtenerPublicacionesPorZona(
            String zona);
}