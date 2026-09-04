package com.uade.tpo.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de una imagen de vehiculo.
 * "file" viene en base64
 * En el front se muestra con: <img src={`data:image/jpeg;base64,${file}`} />
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenVehiculoResponse {
    private Long id;
    private Long idVehiculo;
    private Integer orden;
    private String file;
}
