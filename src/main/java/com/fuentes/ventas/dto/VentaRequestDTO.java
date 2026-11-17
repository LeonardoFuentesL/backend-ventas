package com.fuentes.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

// Datos de entrada para registrar una nueva venta
public record VentaRequestDTO(

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre del cliente debe tener entre 2 y 100 caracteres")
        @Schema(example = "Juan Pérez")
        String cliente,

        @NotEmpty(message = "La venta debe incluir al menos un producto")
        @Valid
        List<DetalleVentaRequestDTO> detalles
) {
}
