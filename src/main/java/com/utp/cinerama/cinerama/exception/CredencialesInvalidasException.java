package com.utp.cinerama.cinerama.exception;

/**
 * Excepción lanzada cuando las credenciales son inválidas
 */
public class CredencialesInvalidasException extends RuntimeException {
    
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
    
    public CredencialesInvalidasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
