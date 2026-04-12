package com.factoria.gestionproductos.service;

import com.factoria.gestionproductos.domain.Producto;
import com.factoria.gestionproductos.domain.TarifaProduccion;
import com.factoria.gestionproductos.dto.TarifaProduccionForm;
import com.factoria.gestionproductos.repository.TarifaProduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TarifaProduccionService {

    private final TarifaProduccionRepository tarifaProduccionRepository;

    public TarifaProduccionService(TarifaProduccionRepository tarifaProduccionRepository) {
        this.tarifaProduccionRepository = tarifaProduccionRepository;
    }

    @Transactional(readOnly = true)
    public TarifaProduccion buscarTarifaActual(Long productoId) {
        return tarifaProduccionRepository.findFirstByProductoIdAndActivaTrueOrderByFechaVigenciaDesdeDescIdDesc(productoId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public BigDecimal buscarMontoActual(Long productoId) {
        TarifaProduccion tarifa = buscarTarifaActual(productoId);
        return tarifa != null ? tarifa.getMonto() : null;
    }

    @Transactional(readOnly = true)
    public List<TarifaProduccion> historialPorProducto(Long productoId) {
        return tarifaProduccionRepository.findByProductoIdOrderByFechaVigenciaDesdeDescIdDesc(productoId);
    }

    @Transactional
    public TarifaProduccion registrarTarifaInicial(Producto producto, BigDecimal monto) {
        TarifaProduccion tarifa = new TarifaProduccion();
        tarifa.setProducto(producto);
        tarifa.setMonto(monto);
        tarifa.setActiva(true);
        tarifa.setFechaVigenciaDesde(LocalDate.now());
        tarifa.setObservacion("Tarifa inicial del producto");
        return tarifaProduccionRepository.save(tarifa);
    }

    @Transactional
    public TarifaProduccion cambiarTarifa(Producto producto, TarifaProduccionForm form) {
        TarifaProduccion actual = buscarTarifaActual(producto.getId());
        if (actual != null) {
            actual.setActiva(false);
            actual.setFechaVigenciaHasta(LocalDate.now());
            tarifaProduccionRepository.save(actual);
        }

        TarifaProduccion nueva = new TarifaProduccion();
        nueva.setProducto(producto);
        nueva.setMonto(form.getMonto());
        nueva.setActiva(true);
        nueva.setFechaVigenciaDesde(LocalDate.now());
        nueva.setObservacion(form.getObservacion());
        return tarifaProduccionRepository.save(nueva);
    }

    @Transactional(readOnly = true)
    public TarifaProduccionForm convertirAForm(Long productoId) {
        TarifaProduccion tarifa = buscarTarifaActual(productoId);
        if (tarifa == null) {
            throw new EntityNotFoundException("No se encontró una tarifa activa para el producto solicitado");
        }
        TarifaProduccionForm form = new TarifaProduccionForm();
        form.setMonto(tarifa.getMonto());
        form.setObservacion(tarifa.getObservacion());
        return form;
    }
}
