package com.utp.cinerama.cinerama.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DTO para validación de token JWT
 * Usado en endpoint /api/auth/validate
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationDTO {
    private Boolean valido;
    private String username;
    private List<String> roles;
    private String mensaje;
    
    // ⏰ Información de expiración
    private Long minutosRestantes;
    private Date fechaExpiracion;
}
