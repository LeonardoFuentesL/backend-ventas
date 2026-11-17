package com.fuentes.ventas.exception;

// Excepción personalizada cuando no alcanza el stock para realizar una venta
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String message) {
        super(message);
    }
}
