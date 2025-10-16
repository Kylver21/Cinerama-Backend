package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.dto.*;
import com.utp.cinerama.cinerama.model.Usuario;
import com.utp.cinerama.cinerama.security.CustomUserDetailsService;
import com.utp.cinerama.cinerama.service.UsuarioService;
import com.utp.cinerama.cinerama.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de autenticación - PATRÓN SIMPLIFICADO
 * Maneja login, registro y operaciones de JWT con BCrypt
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * 📝 POST /api/auth/register
     * Registrar un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<MensajeDTO> registrar(@Valid @RequestBody RegistroDTO registroDTO) {
        log.info("📝 Solicitud de registro para username: {}", registroDTO.getUsername());

        Usuario usuario = usuarioService.registrar(registroDTO);

        MensajeDTO respuesta = MensajeDTO.builder()
                .mensaje("Usuario registrado exitosamente con ID: " + usuario.getId())
                .exitoso(true)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * 🔐 POST /api/auth/login
     * Autenticar usuario y obtener JWT token
     * 
     * FLUJO:
     * 1. AuthenticationManager autentica con BCrypt
     * 2. Si es exitoso, carga detalles del usuario
     * 3. Extrae roles del UserDetails
     * 4. Genera token JWT con JwtUtil
     * 5. Opcionalmente crea cookie para remember me
     * 6. Retorna token + userId + clienteId + roles
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginDTO loginDTO,
            @RequestParam(required = false, defaultValue = "false") boolean rememberMe,
            HttpServletResponse response) {

        log.info("🔐 Intento de login: {}", loginDTO.getUsername());

        try {
            // 1. Autenticar con Spring Security (BCrypt automático)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );

            log.info("✅ Autenticación exitosa para: {}", loginDTO.getUsername());

            // 2. Cargar detalles del usuario
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuario = userDetailsService.obtenerUsuarioCompleto(userDetails.getUsername());

            // 3. Extraer SOLO roles (prefijo ROLE_), no permisos
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .filter(name -> name.startsWith("ROLE_"))
                    .collect(Collectors.toList());

            // 4. Generar token JWT con la versión simple (username + rol)
            String rol = roles.isEmpty() ? "" : roles.get(0);
            String jwt = jwtUtil.generateToken(usuario.getUsername(), rol);

            log.info("🎫 Token JWT generado para: {} con roles: {}", usuario.getUsername(), roles);

            // 5. Configurar cookie si rememberMe está activo
            if (rememberMe) {
                Cookie cookie = new Cookie("jwt", jwt);
                cookie.setHttpOnly(true); // No accesible por JavaScript (seguridad)
                cookie.setPath("/");      // Disponible en toda la app
                cookie.setMaxAge(60 * 60 * 24 * 7); // 7 días
                // cookie.setSecure(true); // DESCOMENTAR EN PRODUCCIÓN (HTTPS)
                response.addCookie(cookie);
                log.info("🍪 Cookie JWT configurada con remember me");
            }

            // 6. Construir respuesta con toda la información
            LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                    .token(jwt)
                    .tipo("Bearer")
                    .username(usuario.getUsername())
                    .email(usuario.getEmail())
                    .roles(roles)
                    .userId(usuario.getId())
                    .clienteId(usuario.getCliente() != null ? usuario.getCliente().getId() : null)
                    .nombreCompleto(usuario.getCliente() != null ? 
                        usuario.getCliente().getNombre() + " " + usuario.getCliente().getApellido() : 
                        null)
                    .build();

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException e) {
            log.error("❌ Credenciales inválidas para: {}", loginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MensajeDTO.builder()
                            .mensaje("Credenciales inválidas. Verifica tu usuario y contraseña.")
                            .exitoso(false)
                            .build());

        } catch (Exception e) {
            log.error("❌ Error en login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MensajeDTO.builder()
                            .mensaje("Error de autenticación: " + e.getMessage())
                            .exitoso(false)
                            .build());
        }
    }

    /**
     * 👤 GET /api/auth/me
     * Obtener información del usuario autenticado actual
     */
        @GetMapping("/me")
    public ResponseEntity<UsuarioInfoDTO> obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("👤 Obteniendo información del usuario: {}", username);

        Usuario usuario = usuarioService.obtenerPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioInfoDTO info = UsuarioInfoDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(usuario.getNombresRoles())
                .activo(usuario.getActivo())
                .build();

        // Agregar información del cliente si existe
        if (usuario.getCliente() != null) {
            info.setClienteId(usuario.getCliente().getId());
            info.setNombreCompleto(
                    usuario.getCliente().getNombre() + " " + 
                    usuario.getCliente().getApellido()
            );
            info.setPuntosAcumulados(usuario.getCliente().getPuntosAcumulados());
            info.setNivelFidelizacion(usuario.getCliente().getNivelFidelizacion());
        }

        return ResponseEntity.ok(info);
    }

    /**
     * 🔄 POST /api/auth/cambiar-password
     * Cambiar contraseña del usuario actual
     */
        @PostMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("🔑 Cambio de contraseña para usuario: {}", username);

        Usuario usuario = usuarioService.obtenerPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioService.cambiarPassword(
                usuario.getId(), 
                dto.getPasswordActual(), 
                dto.getPasswordNueva()
        );

        MensajeDTO respuesta = MensajeDTO.builder()
                .mensaje("Contraseña actualizada exitosamente")
                .exitoso(true)
                .build();

        return ResponseEntity.ok(respuesta);
    }

    /**
     * 🚪 POST /api/auth/logout
     * Logout (invalida cookie si existe)
     */
    @PostMapping("/logout")
    public ResponseEntity<MensajeDTO> logout(HttpServletResponse response) {
        // Eliminar cookie JWT
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Eliminar inmediatamente
        response.addCookie(cookie);

        // Limpiar contexto de seguridad
        SecurityContextHolder.clearContext();

        log.info("👋 Sesión cerrada, cookie eliminada");

        MensajeDTO respuesta = MensajeDTO.builder()
                .mensaje("Sesión cerrada exitosamente")
                .exitoso(true)
                .build();

        return ResponseEntity.ok(respuesta);
    }

    /**
     * ✅ GET /api/auth/validate
     * Validar token JWT
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validarToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(TokenValidationDTO.builder()
                                .valido(false)
                                .mensaje("Falta o es inválido el header Authorization con esquema Bearer")
                                .build());
            }

            String token = authHeader.substring(7); // Remover "Bearer "
            boolean valido = jwtUtil.validateToken(token);

                        if (valido) {
                                String username = jwtUtil.extractUsername(token);
                                String rol = jwtUtil.extractRol(token);
                                List<String> roles = (rol != null && !rol.isEmpty())
                                                ? java.util.List.of(rol)
                                                : java.util.List.of();

                return ResponseEntity.ok(TokenValidationDTO.builder()
                        .valido(true)
                        .username(username)
                        .roles(roles)
                        .mensaje("Token válido")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(TokenValidationDTO.builder()
                                .valido(false)
                                .mensaje("Token inválido o expirado")
                                .build());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenValidationDTO.builder()
                            .valido(false)
                            .mensaje("Error al validar token: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 🔄 POST /api/auth/refresh
     * Refrescar token (generar nuevo token antes de que expire)
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
                        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(MensajeDTO.builder()
                                                                .exitoso(false)
                                                                .mensaje("Falta o es inválido el header Authorization con esquema Bearer")
                                                                .build());
                        }

                        String oldToken = authHeader.substring(7);
            
            // Validar token actual
            if (!jwtUtil.validateToken(oldToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(MensajeDTO.builder()
                                .mensaje("Token inválido o expirado")
                                .exitoso(false)
                                .build());
            }

            // Extraer información del token anterior
            String username = jwtUtil.extractUsername(oldToken);
            String rol = jwtUtil.extractRol(oldToken);
            // Carga del usuario para armar respuesta consistente
            Usuario usuario = usuarioService.obtenerPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            List<String> roles = usuario.getNombresRoles() != null
                    ? new java.util.ArrayList<>(usuario.getNombresRoles())
                    : java.util.Collections.emptyList();

            // Generar nuevo token
            String newToken = jwtUtil.generateToken(username, rol);

            log.info("🔄 Token refrescado para: {}", username);

            return ResponseEntity.ok(LoginResponseDTO.builder()
                    .token(newToken)
                    .tipo("Bearer")
                    .username(username)
                    .email(usuario.getEmail())
                    .roles(roles)
                    .userId(usuario.getId())
                    .build());

        } catch (Exception e) {
            log.error("❌ Error al refrescar token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MensajeDTO.builder()
                            .mensaje("Error al refrescar token")
                            .exitoso(false)
                            .build());
        }
    }

    /**
     * ✅ GET /api/auth/validar-username/{username}
     * Verificar si un username está disponible
     */
    @GetMapping("/validar-username/{username}")
    public ResponseEntity<DisponibilidadDTO> validarUsername(@PathVariable String username) {
        Boolean disponible = !usuarioService.existePorUsername(username);

        DisponibilidadDTO respuesta = DisponibilidadDTO.builder()
                .disponible(disponible)
                .mensaje(disponible ? "Username disponible" : "Username ya está en uso")
                .build();

        return ResponseEntity.ok(respuesta);
    }

    /**
     * ✅ GET /api/auth/validar-email/{email}
     * Verificar si un email está disponible
     */
    @GetMapping("/validar-email/{email}")
    public ResponseEntity<DisponibilidadDTO> validarEmail(@PathVariable String email) {
        Boolean disponible = !usuarioService.existePorEmail(email);

        DisponibilidadDTO respuesta = DisponibilidadDTO.builder()
                .disponible(disponible)
                .mensaje(disponible ? "Email disponible" : "Email ya está registrado")
                .build();

        return ResponseEntity.ok(respuesta);
    }

    // ========== DTOs internos ==========

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UsuarioInfoDTO {
        private Long id;
        private String username;
        private String email;
        private java.util.Set<String> roles;
        private Boolean activo;
        
        // Información del cliente (si existe)
        private Long clienteId;
        private String nombreCompleto;
        private Integer puntosAcumulados;
        private String nivelFidelizacion;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CambiarPasswordDTO {
        @jakarta.validation.constraints.NotBlank(message = "La contraseña actual es obligatoria")
        private String passwordActual;
        
        @jakarta.validation.constraints.NotBlank(message = "La contraseña nueva es obligatoria")
        @jakarta.validation.constraints.Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
        private String passwordNueva;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DisponibilidadDTO {
        private Boolean disponible;
        private String mensaje;
    }
}
