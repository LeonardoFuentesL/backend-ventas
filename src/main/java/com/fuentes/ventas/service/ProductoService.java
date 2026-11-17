package com.fuentes.ventas.service;

import com.fuentes.ventas.dto.ProductoRequestDTO;
import com.fuentes.ventas.dto.ProductoResponseDTO;
import com.fuentes.ventas.exception.ResourceNotAvailable;
import com.fuentes.ventas.model.Producto;
import com.fuentes.ventas.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Lógica de negocio para el módulo de Productos
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtiene todos los productos registrados y los convierte a DTO
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
        List<Producto> productos = productoRepository.findAll();
        List<ProductoResponseDTO> respuesta = new ArrayList<>();

        for (Producto producto : productos) {
            respuesta.add(toResponseDTO(producto));
        }

        return respuesta;
    }

    // Busca un producto por su ID
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = buscarEntidadPorId(id);
        return toResponseDTO(producto);
    }

    // Crea y guarda un nuevo producto en la base de datos
    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO requestDTO) {
        Producto nuevoProducto = toEntity(requestDTO);
        Producto productoGuardado = productoRepository.save(nuevoProducto);
        return toResponseDTO(productoGuardado);
    }

    // Actualiza los datos de un producto existente
    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO requestDTO) {
        Producto productoExistente = buscarEntidadPorId(id);

        productoExistente.setNombre(requestDTO.nombre());
        productoExistente.setDescripcion(requestDTO.descripcion());
        productoExistente.setPrecio(requestDTO.precio());
        productoExistente.setStock(requestDTO.stock());

        Producto productoActualizado = productoRepository.save(productoExistente);
        return toResponseDTO(productoActualizado);
    }

    // Elimina un producto por su ID si existe
    @Transactional
    public void eliminar(Long id) {
        Producto productoExistente = buscarEntidadPorId(id);
        productoRepository.deleteById(productoExistente.getId());
    }

    // --- Métodos auxiliares sencillos ---

    private Producto buscarEntidadPorId(Long id) {
        Optional<Producto> opcional = productoRepository.findById(id);
        if (opcional.isEmpty()) {
            throw new ResourceNotAvailable("No se encontró el producto con ID: " + id);
        }
        return opcional.get();
    }

    private Producto toEntity(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
        return producto;
    }

    private ProductoResponseDTO toResponseDTO(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}
