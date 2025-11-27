package com.utp.cinerama.cinerama.exception;

import com.utp.cinerama.cinerama.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones
 * Captura todas las excepciones de la aplicacion y las convierte en respuestas JSON
 * consistentes para el frontend Angular
 * 
 * Beneficios:
 * 1. Codigo limpio en controllers (no mas try-catch)
 * 2. Respuestas consistentes para Angular
 * 3. Logging centralizado de errores
 * 4. Mejor UX con mensajes claros
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validacion (@Valid en controllers)
     * 
     * Cuando: Un DTO no pasa las validaciones (ej: @NotBlank, @Email, etc.)
     * Retorna: 400 BAD_REQUEST con detalles por campo
     * 
     * Ejemplo en Angular:
     * {
     *   "code": "VALIDATION_ERROR",
     *   "message": "Errores de validacion",
     *   "details": {
     *     "email": "El email debe ser valido",
     *     "password": "La contraseña debe tener al menos 6 caracteres"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.warn("Errores de validacion en {}: {}", request.getRequestURI(), ex.getMessage());
        
        // Extraer errores campo por campo
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Errores de validacion en los datos enviados")
                .details(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja recursos no encontrados
     * 
     * Cuando: Se busca un recurso que no existe en BD
     * Retorna: 404 NOT_FOUND
     * 
     * Ejemplo: Pelicula con ID 999 no existe
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja solicitudes incorrectas
     * 
     * Cuando: La solicitud del cliente viola reglas de negocio
     * Retorna: 400 BAD_REQUEST
     * 
     * Ejemplo: Intentar reservar un asiento ya ocupado
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {
        
        log.warn("Solicitud incorrecta en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja errores de autenticacion
     * 
     * Cuando: Token invalido, expirado o credenciales incorrectas
     * Retorna: 401 UNAUTHORIZED
     * 
     * Ejemplo: Token JWT expirado
     */
    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            Exception ex,
            HttpServletRequest request) {
        
        log.warn("Error de autenticacion en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Credenciales invalidas o token expirado")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Maneja errores de autorizacion (permisos)
     * 
     * Cuando: Usuario autenticado pero sin permisos
     * Retorna: 403 FORBIDDEN
     * 
     * Ejemplo: Cliente intentando acceder a endpoint de ADMIN
     */
    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(
            Exception ex,
            HttpServletRequest request) {
        
        log.warn("Acceso denegado en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("FORBIDDEN")
                .message("No tienes permisos para acceder a este recurso")
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Maneja cualquier otro error no esperado
     * 
     * Cuando: Error no manejado especificamente
     * Retorna: 500 INTERNAL_SERVER_ERROR
     * 
     * IMPORTANTE: Loggea el stack trace completo para debugging
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Error no manejado en {}: ", request.getRequestURI(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("Ha ocurrido un error interno. Por favor contacta al administrador.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja errores de conversion de tipos
     * 
     * Cuando: Parametro de URL con tipo incorrecto
     * Retorna: 400 BAD_REQUEST
     * 
     * Ejemplo: /api/peliculas/abc (se espera numero)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.warn("Argumento ilegal en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INVALID_ARGUMENT")
                .message("Parametro invalido en la solicitud")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja ResponseStatusException (usadas en servicios para errores de negocio)
     * 
     * Cuando: Se lanza ResponseStatusException con código y mensaje específico
     * Retorna: El código HTTP especificado en la excepción
     * 
     * Ejemplos:
     * - 409 CONFLICT: "El username 'cliente1' ya está en uso"
     * - 409 CONFLICT: "El email 'cliente1@email.com' ya está registrado"
     * - 404 NOT_FOUND: "Rol ROLE_CLIENTE no encontrado"
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String mensaje = ex.getReason() != null ? ex.getReason() : "Error en la solicitud";
        
        log.warn("ResponseStatusException en {}: {} - {}", 
                 request.getRequestURI(), status, mensaje);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(status.name())
                .message(mensaje)
                .status(status.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Maneja errores de integridad de datos (constraints de BD)
     * 
     * Cuando: Se viola un constraint UNIQUE en la base de datos
     * Retorna: 409 CONFLICT con mensaje descriptivo
     * 
     * Ejemplos:
     * - Teléfono duplicado
     * - Número de documento duplicado
     * - Email duplicado (si no se valida antes)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        
        String mensaje = "Error de integridad de datos";
        String detalle = ex.getMostSpecificCause().getMessage();
        
        // Extraer información útil del error
        if (detalle != null) {
            if (detalle.contains("telefono") || detalle.contains("UKdtxgio8utms5uc0q7ywit7bp7")) {
                mensaje = "El teléfono ya está registrado en el sistema";
            } else if (detalle.contains("email")) {
                mensaje = "El email ya está registrado en el sistema";
            } else if (detalle.contains("numero_documento")) {
                mensaje = "El número de documento ya está registrado en el sistema";
            } else if (detalle.contains("username")) {
                mensaje = "El nombre de usuario ya está en uso";
            } else if (detalle.contains("Duplicate entry")) {
                // Extraer el valor duplicado del mensaje
                mensaje = "Ya existe un registro con ese valor en el sistema";
            }
        }
        
        log.warn("Error de integridad en {}: {}", request.getRequestURI(), detalle);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CONFLICT")
                .message(mensaje)
                .status(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
