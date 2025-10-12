package com.utp.cinerama.cinerama.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Proveedor de JWT para generar y validar tokens
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generar token JWT a partir de la autenticación
     */
    public String generarToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + jwtExpiration);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(fechaExpiracion)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Obtener username del token JWT
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validar token JWT
     */
    public boolean validarToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (MalformedJwtException ex) {
            log.error("❌ Token JWT malformado");
        } catch (ExpiredJwtException ex) {
            log.error("❌ Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            log.error("❌ Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            log.error("❌ JWT claims string está vacío");
        } catch (Exception ex) {
            log.error("❌ Error validando token JWT: {}", ex.getMessage());
        }

        return false;
    }

    /**
     * Obtener fecha de expiración del token
     */
    public Date getFechaExpiracion(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    /**
     * Verificar si el token ha expirado
     */
    public boolean tokenExpirado(String token) {
        try {
            Date fechaExpiracion = getFechaExpiracion(token);
            return fechaExpiracion.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
