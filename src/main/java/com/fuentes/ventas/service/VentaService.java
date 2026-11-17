package com.fuentes.ventas.service;

import com.fuentes.ventas.dto.DetalleVentaRequestDTO;
import com.fuentes.ventas.dto.DetalleVentaResponseDTO;
import com.fuentes.ventas.dto.VentaRequestDTO;
import com.fuentes.ventas.dto.VentaResponseDTO;
import com.fuentes.ventas.exception.ResourceNotAvailable;
import com.fuentes.ventas.exception.StockInsuficienteException;
import com.fuentes.ventas.model.DetalleVenta;
import com.fuentes.ventas.model.Producto;
import com.fuentes.ventas.model.Venta;
import com.fuentes.ventas.repository.ProductoRepository;
import com.fuentes.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Lógica de negocio para el módulo de Ventas
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    // Lista todas las ventas registradas junto con sus detalles
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarTodas() {
        List<Venta> ventas = ventaRepository.findAll();
        List<VentaResponseDTO> respuesta = new ArrayList<>();

        for (Venta venta : ventas) {
            respuesta.add(toResponseDTO(venta));
        }

        return respuesta;
    }

    // Busca una venta por su ID
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerPorId(Long id) {
        Venta venta = buscarVentaPorId(id);
        return toResponseDTO(venta);
    }

    // Registra una nueva venta, valida stock, calcula subtotales/total y descuenta inventario
    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO request) {
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDateTime.now());
        nuevaVenta.setCliente(request.cliente());

        BigDecimal totalVenta = BigDecimal.ZERO;
        List<DetalleVenta> listaDetalles = new ArrayList<>();

        for (DetalleVentaRequestDTO item : request.detalles()) {
            // 1. Buscar el producto en la base de datos
            Optional<Producto> opcionalProducto = productoRepository.findById(item.productoId());
            if (opcionalProducto.isEmpty()) {
                throw new ResourceNotAvailable("No se encontró el producto con ID: " + item.productoId());
            }

            Producto producto = opcionalProducto.get();

            // 2. Validar que haya stock suficiente
            if (producto.getStock() < item.cantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto '" + producto.getNombre()
                                + "'. Disponible: " + producto.getStock()
                                + ", solicitado: " + item.cantidad()
                );
            }

            // 3. Tomar precio actual desde la BD y calcular subtotal
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal cantidadBD = BigDecimal.valueOf(item.cantidad());
            BigDecimal subtotal = precioUnitario.multiply(cantidadBD);
            totalVenta = totalVenta.add(subtotal);

            // 4. Descontar stock y actualizar el producto
            producto.setStock(producto.getStock() - item.cantidad());
            productoRepository.save(producto);

            // 5. Crear el renglón de detalle vinculado a la venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(nuevaVenta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);

            listaDetalles.add(detalle);
        }

        nuevaVenta.setTotal(totalVenta);
        nuevaVenta.setDetalles(listaDetalles);

        Venta ventaGuardada = ventaRepository.save(nuevaVenta);
        return toResponseDTO(ventaGuardada);
    }

    // Elimina una venta y devuelve el stock a cada producto vendido
    @Transactional
    public void eliminar(Long id) {
        Venta ventaExistente = buscarVentaPorId(id);

        for (DetalleVenta detalle : ventaExistente.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        ventaRepository.deleteById(ventaExistente.getId());
    }

    // --- Métodos auxiliares sencillos ---

    private Venta buscarVentaPorId(Long id) {
        Optional<Venta> opcional = ventaRepository.findById(id);
        if (opcional.isEmpty()) {
            throw new ResourceNotAvailable("No se encontró la venta con ID: " + id);
        }
        return opcional.get();
    }

    private VentaResponseDTO toResponseDTO(Venta venta) {
        List<DetalleVentaResponseDTO> detallesDTO = new ArrayList<>();

        for (DetalleVenta detalle : venta.getDetalles()) {
            DetalleVentaResponseDTO detalleDTO = new DetalleVentaResponseDTO(
                    detalle.getId(),
                    detalle.getProducto().getId(),
                    detalle.getProducto().getNombre(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubtotal()
            );
            detallesDTO.add(detalleDTO);
        }

        return new VentaResponseDTO(
                venta.getId(),
                venta.getFecha(),
                venta.getCliente(),
                venta.getTotal(),
                detallesDTO
        );
    }
}
