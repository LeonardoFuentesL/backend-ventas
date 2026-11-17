package com.fuentes.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

// Datos de salida que devuelve la API al consultar productos
public record ProductoResponseDTO(

        @Schema(example = "1")
        Long id,

        @Schema(example = "Cuaderno Profesional 100 hojas")
        String nombre,

        @Schema(example = "Cuaderno de raya tamaño profesional con espiral metálico")
        String descripcion,

        @Schema(example = "45.50")
        BigDecimal precio,

        @Schema(example = "50")
        Integer stock
) {
}
