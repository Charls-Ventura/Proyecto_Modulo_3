package com.factoria.gestionproductos.repository;

import com.factoria.gestionproductos.domain.TarifaProduccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarifaProduccionRepository extends JpaRepository<TarifaProduccion, Long> {

    Optional<TarifaProduccion> findFirstByProductoIdAndActivaTrueOrderByFechaVigenciaDesdeDescIdDesc(Long productoId);

    List<TarifaProduccion> findByProductoIdOrderByFechaVigenciaDesdeDescIdDesc(Long productoId);
}
