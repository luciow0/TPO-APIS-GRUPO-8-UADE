package com.uade.tpo.marketplace.dto;

import java.time.LocalDateTime;

import com.uade.tpo.marketplace.Enum.RolResena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Reseña ya publicada, vista por otras personas.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaPublicaResponse {
    private Long idResena;
    private Long idReserva;
    private RolResena rol;
    private Integer estrellas;
    private String comentario;
    private LocalDateTime fecha;
    private UsuarioResumen autor;
}
