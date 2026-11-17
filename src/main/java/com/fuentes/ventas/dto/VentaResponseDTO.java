package com.fuentes.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Datos de salida que devuelve la API al consultar o crear una venta
public record VentaResponseDTO(

        @Schema(example = "1")
        Long id,

        @Schema(example = "2026-11-16T19:15:00")
        LocalDateTime fecha,

        @Schema(example = "Juan Pérez")
        String cliente,

        @Schema(example = "159.00")
        BigDecimal total,

        List<DetalleVentaResponseDTO> detalles
) {
}
