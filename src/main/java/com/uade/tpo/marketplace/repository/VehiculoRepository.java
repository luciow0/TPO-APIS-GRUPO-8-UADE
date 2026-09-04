package com.uade.tpo.marketplace.repository;

import java.util.Optional; // Para manejar valores que pueden ser nulos

import org.springframework.data.jpa.repository.JpaRepository; // Para manejar la persistencia de datos y realizar operaciones CRUD
import org.springframework.stereotype.Repository; // Para marcar la clase como un repositorio de Spring, lo que permite que Spring la detecte y la maneje como un componente de persistencia

import com.uade.tpo.marketplace.entity.Vehiculo; // Para importar la clase Vehiculo, que es la entidad que se va a manejar en este repositorio

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> { // Se define la interfaz que extiende jpaRepository
    Optional<Vehiculo> findByPatente(String patente); // Es un query method que busca un vehiculo por su pantente
    boolean existsByPatente(String patente); // Es un query method que verifica si existe un vehiculo con la patente dada
}