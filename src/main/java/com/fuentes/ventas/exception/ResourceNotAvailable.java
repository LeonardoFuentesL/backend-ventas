package com.fuentes.ventas.exception;

// Excepción personalizada cuando un registro no existe en la base de datos
public class ResourceNotAvailable extends RuntimeException {

    public ResourceNotAvailable(String message) {
        super(message);
    }
}
