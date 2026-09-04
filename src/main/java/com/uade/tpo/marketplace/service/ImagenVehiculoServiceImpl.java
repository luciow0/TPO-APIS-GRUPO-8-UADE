package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.dto.ImagenVehiculoResponse;
import com.uade.tpo.marketplace.entity.ImagenVehiculo;
import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.exception.EntityNotFoundException;
import com.uade.tpo.marketplace.repository.ImagenVehiculoRepository;
import com.uade.tpo.marketplace.repository.VehiculoRepository;

@Service
public class ImagenVehiculoServiceImpl implements ImagenVehiculoService {

    private final ImagenVehiculoRepository imagenVehiculoRepository;
    private final VehiculoRepository vehiculoRepository;

    public ImagenVehiculoServiceImpl(ImagenVehiculoRepository imagenVehiculoRepository,
            VehiculoRepository vehiculoRepository) {
        this.imagenVehiculoRepository = imagenVehiculoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ImagenVehiculoResponse> listar(Pageable pageable) {
        return imagenVehiculoRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public ImagenVehiculoResponse buscarPorId(Long id) {
        return toResponse(obtener(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ImagenVehiculoResponse> buscarPorVehiculo(Long idVehiculo) {
        return imagenVehiculoRepository.findByVehiculo_IdVehiculoOrderByOrdenAsc(idVehiculo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public ImagenVehiculoResponse guardar(Long idVehiculo, Integer orden, MultipartFile file) {
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new EntityNotFoundException("Vehiculo no encontrado con id: " + idVehiculo));

        ImagenVehiculo imagen = new ImagenVehiculo();
        imagen.setVehiculo(vehiculo);
        imagen.setOrden(orden);
        imagen.setImagen(toBlob(file));

        return toResponse(imagenVehiculoRepository.save(imagen));
    }

    @Transactional
    @Override
    public ImagenVehiculoResponse actualizar(Long id, Integer orden, MultipartFile file) {
        ImagenVehiculo imagen = obtener(id);

        if (orden != null) {
            imagen.setOrden(orden);
        }
        // Solo reemplaza el binario
        if (file != null && !file.isEmpty()) {
            imagen.setImagen(toBlob(file));
        }

        return toResponse(imagenVehiculoRepository.save(imagen));
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        if (!imagenVehiculoRepository.existsById(id)) {
            throw new EntityNotFoundException("Imagen de vehiculo no encontrada con id: " + id);
        }
        imagenVehiculoRepository.deleteById(id);
    }

    private ImagenVehiculo obtener(Long id) {
        return imagenVehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imagen de vehiculo no encontrada con id: " + id));
    }

    // MultipartFile -> Blob
    private Blob toBlob(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es requerido");
        }
        try {
            return new SerialBlob(file.getBytes());
        } catch (IOException | SQLException e) {
            throw new RuntimeException("No se pudo procesar la imagen", e);
        }
    }

    private ImagenVehiculoResponse toResponse(ImagenVehiculo imagen) {
        try {
            Blob blob = imagen.getImagen();
            String base64 = Base64.getEncoder()
                    .encodeToString(blob.getBytes(1, (int) blob.length()));
            return ImagenVehiculoResponse.builder()
                    .id(imagen.getIdImagen())
                    .idVehiculo(imagen.getVehiculo() != null ? imagen.getVehiculo().getIdVehiculo() : null)
                    .orden(imagen.getOrden())
                    .file(base64)
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo leer la imagen", e);
        }
    }
}
