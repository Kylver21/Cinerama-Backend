package com.utp.cinerama.cinerama.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de Spring Security
 * Define reglas de autorización, CORS, CSRF, y filtros JWT
 */
@Configuration
@EnableWebSecurity
// @EnableMethodSecurity // Omitimos uso de @PreAuthorize y similares para esta versión simplificada
@RequiredArgsConstructor
public class SecurityConfig {

    
    private final JwtRequestFilter jwtRequestFilter; 

    /**
     * Configuración principal de seguridad
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // ========== RUTAS PÚBLICAS (sin autenticación) ==========
                        
                        // Autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Películas (consulta pública)
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").permitAll()
                        
                        
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").permitAll()
                        
                        
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").permitAll()
                        
                        
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        
                        
                        .requestMatchers(HttpMethod.GET, "/api/asientos/funcion/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/asientos/estadisticas/**").permitAll()

                        // ========== RUTAS SOLO ADMIN ==========
                        
                        // Gestión de películas
                        .requestMatchers(HttpMethod.POST, "/api/peliculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasRole("ADMIN")
                        
                        // Gestión de salas
                        .requestMatchers(HttpMethod.POST, "/api/salas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/salas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/salas/**").hasRole("ADMIN")
                        
                        // Gestión de funciones
                        .requestMatchers(HttpMethod.POST, "/api/funciones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/funciones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasRole("ADMIN")
                        
                        // Gestión de productos
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        
                        // Gestión de usuarios
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        
                        // Generación de asientos
                        .requestMatchers(HttpMethod.POST, "/api/asientos/generar/**").hasRole("ADMIN")

                        // ========== RUTAS AUTENTICADAS (cualquier usuario logueado) ==========
                        
                        // Compra de boletos
                        .requestMatchers("/api/boletos/**").authenticated()
                        
                        // Reserva de asientos
                        .requestMatchers(HttpMethod.POST, "/api/asientos/reservar/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/asientos/confirmar/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/asientos/liberar/**").authenticated()
                        
                        // Compra de productos
                        .requestMatchers("/api/ventas-producto/**").authenticated()
                        
                        // Pagos
                        .requestMatchers("/api/pagos/**").authenticated()
                        
                        // Clientes (solo sus propios datos)
                        .requestMatchers("/api/clientes/**").authenticated()

                        // ========== CUALQUIER OTRA RUTA REQUIERE AUTENTICACIÓN ==========
                        .anyRequest().authenticated()
                )

                // Configurar gestión de sesiones (STATELESS para JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Agregar filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración de CORS para permitir peticiones desde el frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      
                "http://localhost:4200",      
                "http://localhost:8081",      
                "http://localhost:5173",      
                "*"                           
        ));
        
        
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
       
        configuration.setAllowedHeaders(List.of("*"));
        
        // Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Bean para encriptar contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Nota: No registramos manualmente DaoAuthenticationProvider para evitar usar
    // su constructor deprecado. Spring Boot auto-configura un AuthenticationManager
    // basado en el UserDetailsService y PasswordEncoder presentes en el contexto.

    /**
     * Bean de AuthenticationManager para autenticación manual
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
