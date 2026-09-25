package com.uade.tpo.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private Long idPago;
    private String preferenceId;
    private String initPoint;
}
