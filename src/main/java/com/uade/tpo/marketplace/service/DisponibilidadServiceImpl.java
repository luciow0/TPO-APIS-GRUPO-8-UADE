package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.uade.tpo.marketplace.dto.DisponibilidadRequest;
import com.uade.tpo.marketplace.entity.Disponibilidad;
import com.uade.tpo.marketplace.entity.Publicacion;
import com.uade.tpo.marketplace.exceptions.DisponibilidadNotFoundException;
import com.uade.tpo.marketplace.exceptions.PublicacionNotFoundException;
import com.uade.tpo.marketplace.repository.DisponibilidadRepository;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private PublicacionService publicacionService;

    @Override
    public Disponibilidad crearDisponibilidad(DisponibilidadRequest request)
            throws PublicacionNotFoundException {

        validarRequest(request);

        Publicacion publicacion =
                publicacionService.obtenerPublicacionPorId(
                        request.getIdPublicacion());

        Disponibilidad disponibilidad = new Disponibilidad(
                request.getFechaInicio(),
                request.getFechaFin(),
                publicacion);

        return disponibilidadRepository.save(disponibilidad);
    }

    @Override
    public List<Disponibilidad> obtenerDisponibilidades() {
        return disponibilidadRepository.findAll();
    }

    @Override
    public Disponibilidad obtenerDisponibilidadPorId(Long id)
            throws DisponibilidadNotFoundException {

        return disponibilidadRepository.findById(id)
                .orElseThrow(DisponibilidadNotFoundException::new);
    }

    @Override
    public List<Disponibilidad> obtenerDisponibilidadesPorPublicacion(
            Long idPublicacion)
            throws PublicacionNotFoundException {

        publicacionService.obtenerPublicacionPorId(idPublicacion);

        return disponibilidadRepository
                .findByPublicacion_IdPublicacion(idPublicacion);
    }

    @Override
    public Disponibilidad modificarDisponibilidad(
            Long id,
            DisponibilidadRequest request)
            throws DisponibilidadNotFoundException,
            PublicacionNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadRepository.findById(id)
                        .orElseThrow(
                                DisponibilidadNotFoundException::new);

        validarRequest(request);

        Publicacion publicacion =
                publicacionService.obtenerPublicacionPorId(
                        request.getIdPublicacion());

        disponibilidad.setFechaInicio(
                request.getFechaInicio());

        disponibilidad.setFechaFin(
                request.getFechaFin());

        disponibilidad.setPublicacion(publicacion);

        return disponibilidadRepository.save(disponibilidad);
    }

    @Override
    public void eliminarDisponibilidad(Long id)
            throws DisponibilidadNotFoundException {

        Disponibilidad disponibilidad =
                disponibilidadRepository.findById(id)
                        .orElseThrow(
                                DisponibilidadNotFoundException::new);

        disponibilidadRepository.delete(disponibilidad);
    }

    private void validarRequest(DisponibilidadRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La disponibilidad no puede estar vacia");
        }

        if (request.getFechaInicio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio es obligatoria");
        }

        if (request.getFechaFin() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de fin es obligatoria");
        }

        if (!request.getFechaFin()
                .isAfter(request.getFechaInicio())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de fin debe ser posterior a la fecha de inicio");
        }

        if (request.getFechaInicio()
                .isBefore(LocalDate.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio no puede ser anterior a la fecha actual");
        }

        if (request.getIdPublicacion() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La publicacion es obligatoria");
        }
    }
}