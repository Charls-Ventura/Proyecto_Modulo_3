package com.factoria.gestionproductos.repository;

import com.factoria.gestionproductos.domain.MovimientoProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoProductoRepository extends JpaRepository<MovimientoProducto, Long> {

    boolean existsByProductoId(Long productoId);
}
