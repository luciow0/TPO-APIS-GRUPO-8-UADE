package com.uade.tpo.marketplace.dto;

import java.time.LocalDateTime;

import com.uade.tpo.marketplace.Enum.RolResena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Reseña vista por su autor.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponse {
    private Long idResena;
    private Long idReserva;
    private Long idAutor;
    private Long idDestinatario;
    private RolResena rol;
    private Integer estrellas;
    private String comentario;
    private LocalDateTime fecha;
    private boolean publicada;
    // La otra parte (quien recibe la reseña), para mostrar su nombre al editar.
    private UsuarioResumen otraParte;
}
