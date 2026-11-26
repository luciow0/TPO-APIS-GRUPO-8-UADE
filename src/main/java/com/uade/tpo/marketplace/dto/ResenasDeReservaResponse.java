package com.uade.tpo.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenasDeReservaResponse {
    private ResenaResponse mia;
    // null mientras la reseña de la otra parte no esté publicada.
    private ResenaPublicaResponse deLaOtraParte;
    private boolean otraParteYaCalifico;
}
