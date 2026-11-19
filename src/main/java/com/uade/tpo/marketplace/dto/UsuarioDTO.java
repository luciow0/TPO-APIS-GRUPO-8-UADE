package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uade.tpo.marketplace.Enum.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String telefono;
    private LocalDate fechaNacimiento;

    // Solo lectura: si se aceptara en POST/PUT /usuarios, cualquiera podria hacerse ADMIN.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;
}