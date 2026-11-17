package com.fuentes.ventas;

import com.fuentes.ventas.dto.DetalleVentaRequestDTO;
import com.fuentes.ventas.dto.VentaRequestDTO;
import com.fuentes.ventas.dto.VentaResponseDTO;
import com.fuentes.ventas.service.VentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de integración para los endpoints del módulo Ventas
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VentaService ventaService;

    // POST /api/ventas -> 201 Created, calcula el total y descuenta el stock del producto
    @Test
    void debeCrearVentaValidaYDescontarStock() throws Exception {
        String bodyVenta = """
                {
                  "cliente": "Juan Pérez",
                  "detalles": [
                    { "productoId": 1, "cantidad": 2 },
                    { "productoId": 2, "cantidad": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyVenta))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.cliente").value("Juan Pérez"))
                .andExpect(jsonPath("$.total").value(159.00))
                .andExpect(jsonPath("$.detalles.length()").value(2));

        // Verificar que el stock del producto ID 1 bajó de 50 a 48
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(48));
    }

    // POST /api/ventas -> 400 Bad Request cuando el stock no alcanza
    @Test
    void debeRechazarVentaConStockInsuficiente() throws Exception {
        String bodyStockInsuficiente = """
                {
                  "cliente": "María López",
                  "detalles": [
                    { "productoId": 1, "cantidad": 999 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyStockInsuficiente))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Stock insuficiente"));
    }

    // POST /api/ventas -> 404 Not Found cuando se solicita un producto inexistente
    @Test
    void debeDevolver404EnVentaConProductoInexistente() throws Exception {
        String bodyProductoInexistente = """
                {
                  "cliente": "Carlos Ruiz",
                  "detalles": [
                    { "productoId": 9999, "cantidad": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyProductoInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    // GET /api/ventas -> 200 OK devolviendo las ventas registradas
    @Test
    void debeListarVentas() throws Exception {
        VentaRequestDTO solicitud = new VentaRequestDTO(
                "Ana Gómez",
                List.of(new DetalleVentaRequestDTO(1L, 1))
        );
        ventaService.crear(solicitud);

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cliente").value("Ana Gómez"));
    }

    // GET /api/ventas/{id} -> 200 OK si existe y 404 Not Found si no existe
    @Test
    void debeObtenerVentaPorIdExistenteEInexistente() throws Exception {
        VentaRequestDTO solicitud = new VentaRequestDTO(
                "Luis Torres",
                List.of(new DetalleVentaRequestDTO(2L, 2))
        );
        VentaResponseDTO ventaCreada = ventaService.crear(solicitud);

        mockMvc.perform(get("/api/ventas/" + ventaCreada.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ventaCreada.id()))
                .andExpect(jsonPath("$.cliente").value("Luis Torres"))
                .andExpect(jsonPath("$.total").value(136.00));

        mockMvc.perform(get("/api/ventas/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // DELETE /api/ventas/{id} -> 204 No Content y devuelve el stock al producto
    @Test
    void debeEliminarVentaYDevolverStock() throws Exception {
        VentaRequestDTO solicitud = new VentaRequestDTO(
                "Sofía Méndez",
                List.of(new DetalleVentaRequestDTO(1L, 3))
        );
        VentaResponseDTO ventaCreada = ventaService.crear(solicitud);

        mockMvc.perform(delete("/api/ventas/" + ventaCreada.id()))
                .andExpect(status().isNoContent());

        // Verificar que el stock volvió de 47 a 50
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(50));
    }
}
