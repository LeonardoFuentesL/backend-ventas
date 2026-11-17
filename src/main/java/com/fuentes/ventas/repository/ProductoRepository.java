package com.fuentes.ventas.repository;

import com.fuentes.ventas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio estándar de Spring Data JPA para la entidad Producto
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
