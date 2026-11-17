package com.fuentes.ventas;

import com.fuentes.ventas.dto.ProductoRequestDTO;
import com.fuentes.ventas.dto.ProductoResponseDTO;
import com.fuentes.ventas.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de integración para los endpoints del módulo Productos
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoService productoService;

    // GET /api/productos -> 200 OK con los 5 productos iniciales
    @Test
    void debeListarTodosLosProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].nombre").value("Cuaderno Profesional 100 hojas"));
    }

    // GET /api/productos/1 -> 200 OK cuando el producto existe
    @Test
    void debeObtenerProductoPorIdExistente() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precio").value(45.50))
                .andExpect(jsonPath("$.stock").value(50));
    }

    // GET /api/productos/9999 -> 404 Not Found cuando el producto no existe
    @Test
    void debeDevolver404CuandoProductoNoExiste() throws Exception {
        mockMvc.perform(get("/api/productos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    // POST /api/productos -> 201 Created con datos válidos
    @Test
    void debeCrearProductoValido() throws Exception {
        String body = """
                {
                  "nombre": "Lapicero Negro",
                  "descripcion": "Lapicero de tinta gel punto fino",
                  "precio": 24.50,
                  "stock": 25
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Lapicero Negro"))
                .andExpect(jsonPath("$.precio").value(24.50))
                .andExpect(jsonPath("$.stock").value(25));
    }

    // POST /api/productos -> 400 Bad Request cuando los datos son inválidos
    @Test
    void debeRechazarProductoInvalido() throws Exception {
        String bodyInvalido = """
                {
                  "nombre": "",
                  "descripcion": "Producto sin nombre y con valores negativos",
                  "precio": -10.00,
                  "stock": -5
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.detalles.nombre").exists())
                .andExpect(jsonPath("$.detalles.precio").exists())
                .andExpect(jsonPath("$.detalles.stock").exists());
    }

    // PUT /api/productos/1 -> 200 OK al actualizar un producto existente
    @Test
    void debeActualizarProductoExistente() throws Exception {
        String bodyActualizado = """
                {
                  "nombre": "Cuaderno Profesional Pasta Dura",
                  "descripcion": "Cuaderno actualizado con pasta dura",
                  "precio": 59.00,
                  "stock": 30
                }
                """;

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyActualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cuaderno Profesional Pasta Dura"))
                .andExpect(jsonPath("$.precio").value(59.00))
                .andExpect(jsonPath("$.stock").value(30));
    }

    // DELETE /api/productos/{id} -> 204 No Content al eliminar un producto existente
    @Test
    void debeEliminarProductoExistente() throws Exception {
        ProductoResponseDTO productoTemporal = productoService.crear(
                new ProductoRequestDTO("Regla 30cm", "Regla plástica transparente", new BigDecimal("18.00"), 12)
        );

        mockMvc.perform(delete("/api/productos/" + productoTemporal.id()))
                .andExpect(status().isNoContent());

        // Verificar que al consultarlo después responde 404
        mockMvc.perform(get("/api/productos/" + productoTemporal.id()))
                .andExpect(status().isNotFound());
    }
}
