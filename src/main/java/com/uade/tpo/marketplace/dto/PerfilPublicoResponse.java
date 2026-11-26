package com.uade.tpo.marketplace.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo único que ve cualquier visitante de un perfil: nunca email, teléfono ni fecha de nacimiento.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilPublicoResponse {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String foto;
    private LocalDate fechaRegistro;
}
