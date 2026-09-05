package com.uade.tpo.marketplace.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Genera AuthResponse(String token)
@NoArgsConstructor  // Genera AuthResponse() - requerido por algunos serializadores como Jackson
public class AuthResponse {
    private String token;
}