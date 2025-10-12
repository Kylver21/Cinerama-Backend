package com.utp.cinerama.cinerama.exception;

/**
 * Excepción lanzada cuando se intenta registrar un usuario que ya existe
 */
public class UsuarioYaExisteException extends RuntimeException {
    
    public UsuarioYaExisteException(String mensaje) {
        super(mensaje);
    }
    
    public UsuarioYaExisteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
