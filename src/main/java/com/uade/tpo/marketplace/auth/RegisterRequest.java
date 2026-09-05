package com.uade.tpo.marketplace.auth;

import com.uade.tpo.marketplace.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Role role;
    private LocalDate fechaNacimiento;
    private String telefono;
}
