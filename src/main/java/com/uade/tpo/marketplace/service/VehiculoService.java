package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.marketplace.dto.VehiculoDTO;
import com.uade.tpo.marketplace.entity.Vehiculo;

public interface VehiculoService {

    public Page<VehiculoDTO> listar(Pageable pageable);
    public VehiculoDTO buscarPorId(Long id);
    public VehiculoDTO buscarPorPatente(String patente);
    public VehiculoDTO guardar(Vehiculo vehiculo);
    public VehiculoDTO actualizar(Long id, Vehiculo vehiculo);
    public void eliminar(Long id);
    public boolean existePatente(String patente);

    public Optional<Vehiculo> obtenerVehiculoPorId(Long id);
}
