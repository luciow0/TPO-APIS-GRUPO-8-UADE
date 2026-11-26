package com.uade.tpo.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// idReserva solo se usa al crear; al editar se ignora.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaRequest {
    private Long idReserva;
    private Integer estrellas;
    private String comentario;
}
