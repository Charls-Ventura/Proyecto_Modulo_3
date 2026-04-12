package com.factoria.gestionproductos.repository;

import com.factoria.gestionproductos.domain.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoSkuIgnoreCase(String codigoSku);

    boolean existsByCodigoSkuIgnoreCase(String codigoSku);

    @Query("""
            select case when count(p) > 0 then true else false end
            from Producto p
            where lower(p.codigoSku) = lower(:codigoSku)
              and p.id <> :id
            """)
    boolean existsByCodigoSkuIgnoreCaseAndIdNot(@Param("codigoSku") String codigoSku, @Param("id") Long id);

    @Query("""
            select p
            from Producto p
            where (:termino is null or :termino = ''
                   or lower(p.codigoSku) like lower(concat('%', :termino, '%'))
                   or lower(p.nombre) like lower(concat('%', :termino, '%')))
            """)
    Page<Producto> buscar(@Param("termino") String termino, Pageable pageable);

    @Query("select p.codigoSku from Producto p where p.codigoSku like concat(:prefijo, '-%')")
    List<String> findSkusByPrefijo(@Param("prefijo") String prefijo);
}
