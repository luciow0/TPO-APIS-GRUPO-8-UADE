package com.uade.tpo.marketplace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {

	private Long idVehiculo;

	@NotBlank
	@Size(max = 6)
	private String patente;

	@NotBlank
	@Size(max = 50)
	private String marca;

	@NotBlank
	@Size(max = 50)
	private String modelo;

	@NotNull
	@Min(1900)
	@Max(2026)
	private Integer anio;

	@NotBlank
	@Size(max = 30)
	private String color;

	@NotNull
	@Min(1)
	@Max(9)
	private Integer cantidadAsientos;

	@NotNull
	private Long idUsuario;
}
