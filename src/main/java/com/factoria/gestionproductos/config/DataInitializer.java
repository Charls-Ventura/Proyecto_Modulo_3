package com.factoria.gestionproductos.config;

import com.factoria.gestionproductos.domain.EstadoProducto;
import com.factoria.gestionproductos.domain.MovimientoProducto;
import com.factoria.gestionproductos.domain.Producto;
import com.factoria.gestionproductos.domain.ProductoUnidad;
import com.factoria.gestionproductos.domain.TarifaProduccion;
import com.factoria.gestionproductos.domain.TipoMovimiento;
import com.factoria.gestionproductos.domain.TipoProducto;
import com.factoria.gestionproductos.domain.UnidadMedida;
import com.factoria.gestionproductos.repository.MovimientoProductoRepository;
import com.factoria.gestionproductos.repository.ProductoRepository;
import com.factoria.gestionproductos.repository.ProductoUnidadRepository;
import com.factoria.gestionproductos.repository.TarifaProduccionRepository;
import com.factoria.gestionproductos.repository.UnidadMedidaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ProductoUnidadRepository productoUnidadRepository;
    private final TarifaProduccionRepository tarifaProduccionRepository;

    public DataInitializer(ProductoRepository productoRepository,
                           MovimientoProductoRepository movimientoProductoRepository,
                           UnidadMedidaRepository unidadMedidaRepository,
                           ProductoUnidadRepository productoUnidadRepository,
                           TarifaProduccionRepository tarifaProduccionRepository) {
        this.productoRepository = productoRepository;
        this.movimientoProductoRepository = movimientoProductoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.productoUnidadRepository = productoUnidadRepository;
        this.tarifaProduccionRepository = tarifaProduccionRepository;
    }

    @Override
    public void run(String... args) {
        if (unidadMedidaRepository.count() == 0) {
            UnidadMedida saco = crearUnidad("Saco", "sc");
            UnidadMedida quintal = crearUnidad("Quintal", "qq");
            unidadMedidaRepository.save(saco);
            unidadMedidaRepository.save(quintal);
        }

        if (productoRepository.count() > 0) {
            return;
        }

        UnidadMedida unidadPorDefecto = unidadMedidaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .findFirst()
                .orElse(null);

        Producto p1 = crearProducto("PRD-001", "Arroz Selecto", TipoProducto.PRODUCTO);
        Producto p2 = crearProducto("SUB-001", "Polvillo de Arroz", TipoProducto.SUBPRODUCTO);
        Producto p3 = crearProducto("SUB-002", "Cascarilla", TipoProducto.SUBPRODUCTO);

        productoRepository.save(p1);
        productoRepository.save(p2);
        productoRepository.save(p3);

        tarifaProduccionRepository.save(crearTarifa(p1, new BigDecimal("1550.00"), "Tarifa inicial"));
        tarifaProduccionRepository.save(crearTarifa(p2, new BigDecimal("580.00"), "Tarifa inicial"));
        tarifaProduccionRepository.save(crearTarifa(p3, new BigDecimal("320.00"), "Tarifa inicial"));

        if (unidadPorDefecto != null) {
            productoUnidadRepository.save(crearProductoUnidad(p1, unidadPorDefecto));
            productoUnidadRepository.save(crearProductoUnidad(p2, unidadPorDefecto));
            productoUnidadRepository.save(crearProductoUnidad(p3, unidadPorDefecto));
        }

        MovimientoProducto movimiento = new MovimientoProducto();
        movimiento.setProducto(p1);
        movimiento.setTipo(TipoMovimiento.ENTRADA);
        movimiento.setCantidad(25);
        movimiento.setObservacion("Movimiento de ejemplo para bloquear eliminación del producto PRD-001");
        movimientoProductoRepository.save(movimiento);
    }

    private UnidadMedida crearUnidad(String nombre, String abreviatura) {
        UnidadMedida unidad = new UnidadMedida();
        unidad.setNombre(nombre);
        unidad.setAbreviatura(abreviatura);
        unidad.setActivo(true);
        return unidad;
    }

    private Producto crearProducto(String codigo, String nombre, TipoProducto tipo) {
        Producto producto = new Producto();
        producto.setCodigoSku(codigo);
        producto.setNombre(nombre);
        producto.setTipo(tipo);
        producto.setUnidadPresentacion("Por definir");
        producto.setPrecioUnitario(BigDecimal.ZERO);
        producto.setCantidadInventario(0);
        producto.setDescripcion("Registro base del catalogo de productos");
        producto.setEstado(EstadoProducto.ACTIVO);
        return producto;
    }

    private TarifaProduccion crearTarifa(Producto producto, BigDecimal monto, String observacion) {
        TarifaProduccion tarifa = new TarifaProduccion();
        tarifa.setProducto(producto);
        tarifa.setMonto(monto);
        tarifa.setActiva(true);
        tarifa.setFechaVigenciaDesde(LocalDate.now());
        tarifa.setObservacion(observacion);
        return tarifa;
    }

    private ProductoUnidad crearProductoUnidad(Producto producto, UnidadMedida unidadMedida) {
        ProductoUnidad relacion = new ProductoUnidad();
        relacion.setProducto(producto);
        relacion.setUnidadMedida(unidadMedida);
        relacion.setPrincipal(true);
        relacion.setActivo(true);
        return relacion;
    }
}
