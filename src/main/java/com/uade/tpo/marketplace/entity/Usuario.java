package com.uade.tpo.marketplace.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    private Long idUsuario;

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;

    private LocalDate fechaNacimiento;
}
