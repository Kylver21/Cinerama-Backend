package com.utp.cinerama.cinerama.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que se ejecuta en cada petición HTTP
 * Valida el token JWT y autentica al usuario
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Obtener JWT del header Authorization
            String jwt = obtenerJwtDelRequest(request);

            // 2. Validar y autenticar
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validarToken(jwt)) {
                // 3. Obtener username del token
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                // 4. Cargar detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Establecer autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("✅ Usuario autenticado: {}", username);
            }

        } catch (Exception ex) {
            log.error("❌ No se pudo establecer la autenticación del usuario: {}", ex.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extraer JWT del header Authorization
     * Formato esperado: "Bearer <token>"
     */
    private String obtenerJwtDelRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remover "Bearer " del inicio
        }

        return null;
    }
}
