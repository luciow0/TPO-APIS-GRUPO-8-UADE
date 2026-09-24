package com.uade.tpo.marketplace.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.marketplace.dto.DisponibilidadRequest;
import com.uade.tpo.marketplace.entity.Disponibilidad;
import com.uade.tpo.marketplace.exception.DisponibilidadNotFoundException;
import com.uade.tpo.marketplace.exception.PublicacionNotFoundException;

public interface DisponibilidadService {

    Disponibilidad crearDisponibilidad(DisponibilidadRequest request)
            throws PublicacionNotFoundException;

    Page<Disponibilidad> obtenerDisponibilidades(Pageable pageable);

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
