package com.factoria.gestionproductos.repository;

import com.factoria.gestionproductos.domain.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByAbreviaturaIgnoreCase(String abreviatura);

    List<UnidadMedida> findByActivoTrueOrderByNombreAsc();
}
