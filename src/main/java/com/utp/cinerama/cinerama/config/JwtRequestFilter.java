package com.utp.cinerama.cinerama.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.cinerama.cinerama.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraer token del header Authorization o de cookies
        String jwt = extractJwtFromRequest(request);
        String usernameOrEmail = null;

        // 2. Validar y extraer username del token
        if (jwt != null) {
            try {
                usernameOrEmail = jwtUtil.extractUsername(jwt);
                log.debug("🔑 Token JWT detectado para usuario: {}", usernameOrEmail);
            } catch (ExpiredJwtException e) {
                log.warn("⚠️ Token JWT expirado: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "TOKEN_EXPIRED", 
                    "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
                return;
            } catch (MalformedJwtException e) {
                log.error("❌ Token JWT malformado: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "TOKEN_INVALID", 
                    "Token inválido. Por favor, inicia sesión nuevamente.");
                return;
            } catch (SignatureException e) {
                log.error("❌ Firma de token JWT inválida: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "TOKEN_INVALID_SIGNATURE", 
                    "Token con firma inválida. Por favor, inicia sesión nuevamente.");
                return;
            } catch (Exception e) {
                log.error("❌ Error al extraer username del token: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "TOKEN_ERROR", 
                    "Error al procesar el token. Por favor, inicia sesión nuevamente.");
                return;
            }
        }

        // 3. Si hay username y no está autenticado en el contexto
        if (usernameOrEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            try {
                // 4. Cargar detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);

                // 5. Validar token (firma + expiración)
                if (!jwtUtil.isTokenExpired(jwt)) {
                    log.info("✅ Token válido para usuario: {}", usernameOrEmail);

                    // 6. Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 7. Establecer detalles de la petición (IP, session, etc.)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 8. Establecer autenticación en el contexto de Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("🔐 Usuario autenticado: {} con roles: {}", 
                        usernameOrEmail, 
                        userDetails.getAuthorities()
                    );
                } else {
                    log.warn("⚠️ Token expirado para usuario: {}", usernameOrEmail);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                        "TOKEN_EXPIRED", 
                        "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
                    return;
                }
            } catch (Exception e) {
                log.error("❌ Error al autenticar usuario: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "AUTH_ERROR", 
                    "Error de autenticación. Por favor, inicia sesión nuevamente.");
                return;
            }
        }

        // 9. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Envía una respuesta de error JSON al cliente
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String code, String message) 
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", code);
        errorBody.put("message", message);
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", status);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }

    /**
     * Extraer JWT del header Authorization o de las cookies
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        // 1. Intentar obtener del header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remover "Bearer "
        }

        // 2. Intentar obtener de las cookies (para remember me)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    log.debug("🍪 JWT encontrado en cookie");
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * No filtrar rutas públicas (login, register, etc.)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login") || 
               path.startsWith("/api/auth/register") ||
               path.startsWith("/api/auth/validar-username") ||
               path.startsWith("/api/auth/validar-email") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}
