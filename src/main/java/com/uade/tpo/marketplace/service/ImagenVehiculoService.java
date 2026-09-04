package com.uade.tpo.marketplace.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.dto.ImagenVehiculoResponse;

public interface ImagenVehiculoService {
    Page<ImagenVehiculoResponse> listar(Pageable pageable);
    ImagenVehiculoResponse buscarPorId(Long id);
    List<ImagenVehiculoResponse> buscarPorVehiculo(Long idVehiculo);
    ImagenVehiculoResponse guardar(Long idVehiculo, Integer orden, MultipartFile file);
    ImagenVehiculoResponse actualizar(Long id, Integer orden, MultipartFile file);
    void eliminar(Long id);
}
