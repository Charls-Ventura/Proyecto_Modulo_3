package com.factoria.gestionproductos.repository;

import com.factoria.gestionproductos.domain.ProductoUnidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoUnidadRepository extends JpaRepository<ProductoUnidad, Long> {

    Optional<ProductoUnidad> findFirstByProductoIdAndPrincipalTrueAndActivoTrue(Long productoId);
}
