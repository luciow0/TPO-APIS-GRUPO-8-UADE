package com.uade.tpo.marketplace.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.dto.ImagenVehiculoResponse;
import com.uade.tpo.marketplace.service.ImagenVehiculoService;

@RestController
@RequestMapping("imagen-vehiculo")
public class ImagenVehiculoController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private final ImagenVehiculoService imagenVehiculoService;

    public ImagenVehiculoController(ImagenVehiculoService imagenVehiculoService) {
        this.imagenVehiculoService = imagenVehiculoService;
    }

    @GetMapping
    public ResponseEntity<Page<ImagenVehiculoResponse>> listar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int pageNum = page != null ? page : DEFAULT_PAGE;
        int pageSize = size != null ? size : DEFAULT_SIZE;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return ResponseEntity.ok(imagenVehiculoService.listar(pageable));
    }

    @GetMapping("/por-vehiculo/{idVehiculo}")
    public ResponseEntity<List<ImagenVehiculoResponse>> listarPorVehiculo(@PathVariable Long idVehiculo) {
        return ResponseEntity.ok(imagenVehiculoService.buscarPorVehiculo(idVehiculo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagenVehiculoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(imagenVehiculoService.buscarPorId(id));
    }

    // Subida por multipart/form-data: campo "file" (la imagen) + idVehiculo + orden.
    @PostMapping
    public ResponseEntity<ImagenVehiculoResponse> crear(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idVehiculo") Long idVehiculo,
            @RequestParam("orden") Integer orden) {
        ImagenVehiculoResponse creada = imagenVehiculoService.guardar(idVehiculo, orden, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // Actualiza el orden y/o reemplaza la imagen (el archivo es opcional).
    @PutMapping("/{id}")
    public ResponseEntity<ImagenVehiculoResponse> actualizar(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "orden", required = false) Integer orden) {
        return ResponseEntity.ok(imagenVehiculoService.actualizar(id, orden, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        imagenVehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
