package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.dto.DisponibilidadRequest;
import com.uade.tpo.marketplace.entity.Disponibilidad;
import com.uade.tpo.marketplace.exception.DisponibilidadNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;

public interface DisponibilidadService {

    Disponibilidad crearDisponibilidad(DisponibilidadRequest request)
            throws PublicacionNotFoundException;

    List<Disponibilidad> obtenerDisponibilidades();

    Disponibilidad obtenerDisponibilidadPorId(Long id)
            throws DisponibilidadNotFoundException;

    List<Disponibilidad> obtenerDisponibilidadesPorPublicacion(
            Long idPublicacion)
            throws PublicacionNotFoundException;

    Disponibilidad modificarDisponibilidad(
            Long id,
            DisponibilidadRequest request)
            throws DisponibilidadNotFoundException,
            PublicacionNotFoundException;

    void eliminarDisponibilidad(Long id)
            throws DisponibilidadNotFoundException;
}