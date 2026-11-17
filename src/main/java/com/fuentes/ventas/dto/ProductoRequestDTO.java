package com.fuentes.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// Datos de entrada para crear o actualizar un producto
public record ProductoRequestDTO(

        @NotBlank(message = "El nombre del producto es obligatorio y no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Schema(example = "Cuaderno Profesional 100 hojas")
        String nombre,

        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        @Schema(example = "Cuaderno de raya tamaño profesional con espiral metálico")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
        @Schema(example = "45.50")
        BigDecimal precio,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        @Schema(example = "50")
        Integer stock
) {
}
