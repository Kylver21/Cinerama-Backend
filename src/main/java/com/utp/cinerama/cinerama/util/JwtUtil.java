package com.utp.cinerama.cinerama.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para generar y validar tokens JWT
 * Patrón simplificado siguiendo estándares de la industria
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Obtener la clave de firma desde el secret (Base64)
     * Este método centraliza la obtención de la clave para evitar repetición de código
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generar token JWT con claims personalizados
     * 
     * @param username El username del usuario
     * @param roles Lista de roles del usuario (ROLE_ADMIN, ROLE_CLIENTE, etc.)
     * @param userId ID del usuario en la base de datos
     * @param email Email del usuario
     * @return Token JWT firmado
     */
    public String generateToken(String username, List<String> roles, Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);    // Almacenar roles en el token
        claims.put("userId", userId);  // Almacenar ID del usuario
        claims.put("email", email);    // Almacenar email
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)              // Datos personalizados
                .setSubject(username)           // Username como subject
                .setIssuedAt(now)               // Fecha de creación
                .setExpiration(expiryDate)      // Fecha de expiración
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Firma con HS512
                .compact();                     // Convertir a String
    }

    /**
     * Extraer todos los claims (datos) del token
     * Este método es la base para extraer información específica
     */
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("❌ Token expirado: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("❌ Token malformado: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("❌ Token no soportado: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("❌ JWT claims string vacío: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Error al extraer claims: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extraer username del token
     * El username está almacenado en el "subject" del JWT
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extraer roles del token
     * Los roles están almacenados en los claims personalizados
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) extractAllClaims(token).get("roles");
    }

    /**
     * Extraer ID del usuario del token
     */
    public Long extractUserId(String token) {
        Object userIdObj = extractAllClaims(token).get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        }
        return (Long) userIdObj;
    }

    /**
     * Extraer email del token
     */
    public String extractEmail(String token) {
        return (String) extractAllClaims(token).get("email");
    }

    /**
     * Verificar si el token ha expirado
     * Compara la fecha de expiración con la fecha actual
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // Token expirado
        } catch (Exception e) {
            log.error("❌ Error al verificar expiración: {}", e.getMessage());
            return true; // Por seguridad, considerarlo expirado
        }
    }

    /**
     * Validar token completamente
     * Verifica que el token sea válido y no haya expirado
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // Intenta parsear el token
            return !isTokenExpired(token); // Verifica que no haya expirado
        } catch (Exception e) {
            log.error("❌ Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtener fecha de expiración del token
     */
    public Date getExpirationDate(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Verificar si un token contiene un rol específico
     * Útil para validaciones de autorización
     */
    public boolean hasRole(String token, String role) {
        List<String> roles = extractRoles(token);
        return roles != null && roles.contains(role);
    }

    /**
     * Obtener tiempo restante del token en milisegundos
     */
    public long getTimeToExpiration(String token) {
        Date expiration = getExpirationDate(token);
        return expiration.getTime() - new Date().getTime();
    }
}
