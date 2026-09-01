package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.entity.ImagenVehiculo;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.repository.ImagenVehiculoRepository;

@Service
public class ImagenVehiculoServiceImpl implements ImagenVehiculoService {

    private final ImagenVehiculoRepository imagenVehiculoRepository;

    public ImagenVehiculoServiceImpl(ImagenVehiculoRepository imagenVehiculoRepository) {
        this.imagenVehiculoRepository = imagenVehiculoRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ImagenVehiculo> listar(Pageable pageable) {
        return imagenVehiculoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ImagenVehiculo> buscarPorId(Long id) {
        return imagenVehiculoRepository.findById(id);
    }

    
    @Transactional(readOnly = true)
    @Override
    public List<ImagenVehiculo> buscarPorVehiculo(Long idVehiculo) {
        return imagenVehiculoRepository.findByVehiculo_IdVehiculoOrderByOrdenAsc(idVehiculo);
    }

    @Transactional
    @Override
    public ImagenVehiculo guardar(ImagenVehiculo imagenVehiculo) {
        return imagenVehiculoRepository.save(imagenVehiculo);
    }

    @Transactional
    @Override
    public ImagenVehiculo actualizar(Long id, ImagenVehiculo imagenVehiculo) {
        ImagenVehiculo imagenExistente = imagenVehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imagen del vehiculo no encontrada con id: " + id));

        imagenExistente.setUrl(imagenVehiculo.getUrl());
        imagenExistente.setOrden(imagenVehiculo.getOrden());

        if (imagenVehiculo.getVehiculo() != null) {
            imagenExistente.setVehiculo(imagenVehiculo.getVehiculo());
        }

        return imagenVehiculoRepository.save(imagenExistente);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        imagenVehiculoRepository.deleteById(id);
    }
}