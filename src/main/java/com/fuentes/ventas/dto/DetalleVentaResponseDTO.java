package com.fuentes.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

// Datos de salida para cada producto vendido en el detalle de una venta
public record DetalleVentaResponseDTO(

        @Schema(example = "1")
        Long id,

        @Schema(example = "1")
        Long productoId,

        @Schema(example = "Cuaderno Profesional 100 hojas")
        String nombreProducto,

        @Schema(example = "2")
        Integer cantidad,

        @Schema(example = "45.50")
        BigDecimal precioUnitario,

        @Schema(example = "91.00")
        BigDecimal subtotal
) {
}
