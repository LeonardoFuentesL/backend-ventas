package com.fuentes.ventas.repository;

import com.fuentes.ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio estándar de Spring Data JPA para la entidad Venta
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
}
