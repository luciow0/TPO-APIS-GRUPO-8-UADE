package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.marketplace.entity.ImagenVehiculo;

public interface ImagenVehiculoService {
    Page<ImagenVehiculo> listar(Pageable pageable);
    Optional<ImagenVehiculo> buscarPorId(Long id);
    List<ImagenVehiculo> buscarPorVehiculo(Long idVehiculo);
    ImagenVehiculo guardar(ImagenVehiculo imagenVehiculo);
    ImagenVehiculo actualizar(Long id, ImagenVehiculo imagenVehiculo);
    void eliminar(Long id);
}