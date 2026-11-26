package com.uade.tpo.marketplace.dto;

import com.uade.tpo.marketplace.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos mínimos de la otra parte de una reserva o del autor de una reseña.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResumen {
    private Long idUsuario;
    private String nombre;
    private String apellido;

    public static UsuarioResumen de(Usuario usuario) {
        return new UsuarioResumen(usuario.getIdUsuario(), usuario.getNombre(), usuario.getApellido());
    }
}
