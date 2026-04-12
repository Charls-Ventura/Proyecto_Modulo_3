package com.factoria.gestionproductos.service;

import com.factoria.gestionproductos.domain.Producto;
import com.factoria.gestionproductos.domain.EstadoProducto;
import com.factoria.gestionproductos.domain.ProductoUnidad;
import com.factoria.gestionproductos.domain.TarifaProduccion;
import com.factoria.gestionproductos.domain.TipoProducto;
import com.factoria.gestionproductos.domain.UnidadMedida;
import com.factoria.gestionproductos.dto.ProductoForm;
import com.factoria.gestionproductos.dto.TarifaProduccionForm;
import com.factoria.gestionproductos.exception.ReglaNegocioException;
import com.factoria.gestionproductos.repository.MovimientoProductoRepository;
import com.factoria.gestionproductos.repository.ProductoRepository;
import com.factoria.gestionproductos.repository.ProductoUnidadRepository;
import com.factoria.gestionproductos.repository.UnidadMedidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final TarifaProduccionService tarifaProduccionService;
    private final ProductoUnidadRepository productoUnidadRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           MovimientoProductoRepository movimientoProductoRepository,
                           TarifaProduccionService tarifaProduccionService,
                           ProductoUnidadRepository productoUnidadRepository,
                           UnidadMedidaRepository unidadMedidaRepository) {
        this.productoRepository = productoRepository;
        this.movimientoProductoRepository = movimientoProductoRepository;
        this.tarifaProduccionService = tarifaProduccionService;
        this.productoUnidadRepository = productoUnidadRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    @Transactional(readOnly = true)
    public Page<Producto> listar(String termino, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(Math.max(pagina, 0), tamanio, Sort.by(Sort.Direction.ASC, "id"));
        return productoRepository.buscar(termino == null ? "" : termino.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el producto solicitado"));
    }

    @Transactional
    public Producto crear(ProductoForm form) {
        Producto producto = new Producto();
        producto.setCodigoSku(generarSku(form.getTipo()));
        mapearProducto(producto, form);
        Producto guardado = productoRepository.save(producto);
        tarifaProduccionService.registrarTarifaInicial(guardado, form.getTarifaProduccion());
        asociarUnidadPorDefecto(guardado);
        return guardado;
    }

    @Transactional
    public Producto actualizar(Long id, ProductoForm form) {
        Producto producto = buscarPorId(id);
        mapearProducto(producto, form);
        Producto guardado = productoRepository.save(producto);

        if (form.getTarifaProduccion() != null) {
            TarifaProduccion tarifaActual = tarifaProduccionService.buscarTarifaActual(id);
            if (tarifaActual == null || tarifaActual.getMonto().compareTo(form.getTarifaProduccion()) != 0) {
                TarifaProduccionForm tarifaForm = new TarifaProduccionForm();
                tarifaForm.setMonto(form.getTarifaProduccion());
                tarifaForm.setObservacion("Tarifa actualizada desde editar producto");
                tarifaProduccionService.cambiarTarifa(guardado, tarifaForm);
            }
        }
        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        if (movimientoProductoRepository.existsByProductoId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el producto porque ya tiene movimientos históricos registrados");
        }
        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public ProductoForm convertirAForm(Long id) {
        Producto producto = buscarPorId(id);
        ProductoForm form = new ProductoForm();
        form.setNombre(producto.getNombre());
        form.setTipo(producto.getTipo());
        form.setTarifaProduccion(tarifaProduccionService.buscarMontoActual(id));
        return form;
    }

    @Transactional(readOnly = true)
    public String buscarCodigo(Long id) {
        return buscarPorId(id).getCodigoSku();
    }

    private String generarSku(TipoProducto tipo) {
        String prefijo = tipo == TipoProducto.SUBPRODUCTO ? "SUB" : "PRD";
        List<String> existentes = productoRepository.findSkusByPrefijo(prefijo);
        int max = existentes.stream()
                .map(sku -> {
                    try {
                        return Integer.parseInt(sku.substring(4));
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);
        return String.format("%s-%03d", prefijo, max + 1);
    }

    private void mapearProducto(Producto producto, ProductoForm form) {
        producto.setNombre(form.getNombre().trim());
        producto.setTipo(form.getTipo());
        producto.setPrecioUnitario(form.getTarifaProduccion());
        producto.setCantidadInventario(0);
        producto.setUnidadPresentacion("Por definir");
        producto.setDescripcion("Registro base del catalogo de productos");
        producto.setEstado(EstadoProducto.ACTIVO);
    }

    private void asociarUnidadPorDefecto(Producto producto) {
        UnidadMedida unidadPorDefecto = unidadMedidaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .findFirst()
                .orElse(null);

        if (unidadPorDefecto == null) {
            return;
        }

        ProductoUnidad relacion = new ProductoUnidad();
        relacion.setProducto(producto);
        relacion.setUnidadMedida(unidadPorDefecto);
        relacion.setPrincipal(true);
        relacion.setActivo(true);
        productoUnidadRepository.save(relacion);
    }
}
