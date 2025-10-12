package com.utp.cinerama.cinerama.config;

import com.utp.cinerama.cinerama.filter.JwtRequestFilter;
import com.utp.cinerama.cinerama.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity // Para usar @PreAuthorize en controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter; // RENOMBRADO: JwtAuthenticationFilter → JwtRequestFilter

    /**
     * Configuración principal de seguridad
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no es necesario para APIs REST con JWT)
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
                        
                        // Salas (consulta pública)
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").permitAll()
                        
                        // Funciones (consulta pública)
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").permitAll()
                        
                        // Productos (consulta pública)
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        
                        // Asientos (consulta pública para ver mapa)
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

                // Configurar proveedor de autenticación (usa BCrypt automáticamente)
                .authenticationProvider(authenticationProvider())

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
        
        // Orígenes permitidos (agregar tu frontend)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // React
                "http://localhost:4200",      // Angular
                "http://localhost:8081",      // Vue
                "http://localhost:5173",      // Vite
                "*"                           // Todos (solo para desarrollo)
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Headers permitidos
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

    /**
     * Proveedor de autenticación personalizado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean de AuthenticationManager para autenticación manual
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
