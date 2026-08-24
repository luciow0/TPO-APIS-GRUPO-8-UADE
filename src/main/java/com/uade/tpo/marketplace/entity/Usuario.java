package com.uade.tpo.marketplace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
}
