package com.uade.tpo.marketplace.service;

import java.util.Optional; // Importación de la clase Optional para manejar valores que pueden ser nulos

import org.springframework.data.domain.Page; // Importación de la clase Page para manejar paginación de resultados
import org.springframework.data.domain.Pageable; // Importación de la clase Pageable para manejar la información de paginación

import com.uade.tpo.marketplace.entity.Vehiculo;
import com.uade.tpo.marketplace.dto.VehiculoDTO;

public interface VehiculoService {

    public Page<Vehiculo> listar(Pageable pageable);
    public Optional<Vehiculo> buscarPorId(Long id);
    public Optional<Vehiculo> buscarPorPatente(String patente);
    public Vehiculo guardar(VehiculoDTO vehiculoDTO);
    public Vehiculo actualizar(Long id, VehiculoDTO vehiculoDTO);
    public void eliminar(Long id);
    public boolean existePatente(String patente);

}