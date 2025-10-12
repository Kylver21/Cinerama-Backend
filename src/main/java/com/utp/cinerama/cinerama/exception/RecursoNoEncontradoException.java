package com.utp.cinerama.cinerama.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado
 */
public class RecursoNoEncontradoException extends RuntimeException {
    
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
