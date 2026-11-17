package com.fuentes.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Datos de entrada para cada renglón o producto dentro de una venta
public record DetalleVentaRequestDTO(

        @NotNull(message = "El ID del producto es obligatorio")
        @Schema(example = "1")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        @Schema(example = "2")
        Integer cantidad
) {
}
