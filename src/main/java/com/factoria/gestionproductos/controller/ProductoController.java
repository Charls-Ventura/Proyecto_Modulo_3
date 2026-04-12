package com.factoria.gestionproductos.controller;

import com.factoria.gestionproductos.domain.Producto;
import com.factoria.gestionproductos.domain.TarifaProduccion;
import com.factoria.gestionproductos.domain.TipoProducto;
import com.factoria.gestionproductos.dto.ProductoForm;
import com.factoria.gestionproductos.exception.ReglaNegocioException;
import com.factoria.gestionproductos.service.ProductoService;
import com.factoria.gestionproductos.service.TarifaProduccionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final TarifaProduccionService tarifaProduccionService;

    public ProductoController(ProductoService productoService,
                              TarifaProduccionService tarifaProduccionService) {
        this.productoService = productoService;
        this.tarifaProduccionService = tarifaProduccionService;
    }


    @ModelAttribute("seccionActiva")
    public String seccionActiva() {
        return "productos";
    }
    @ModelAttribute("tiposProducto")
    public TipoProducto[] tiposProducto() {
        return TipoProducto.values();
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String buscar,
                         @RequestParam(defaultValue = "0") int pagina,
                         Model model) {
        Page<Producto> productos = productoService.listar(buscar, pagina, 5);
        Map<Long, BigDecimal> tarifasActuales = new HashMap<>();

        for (Producto producto : productos.getContent()) {
            TarifaProduccion tarifa = tarifaProduccionService.buscarTarifaActual(producto.getId());
            tarifasActuales.put(producto.getId(), tarifa != null ? tarifa.getMonto() : null);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("buscar", buscar);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("tarifasActuales", tarifasActuales);
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("productoForm", new ProductoForm());
        model.addAttribute("tituloFormulario", "Agregar Producto");
        model.addAttribute("accionFormulario", "/productos");
        model.addAttribute("mostrarCodigo", false);
        return "productos/formulario";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("productoForm") ProductoForm productoForm,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tituloFormulario", "Agregar Producto");
            model.addAttribute("accionFormulario", "/productos");
            model.addAttribute("mostrarCodigo", false);
            return "productos/formulario";
        }

        try {
            productoService.crear(productoForm);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto registrado correctamente");
            return "redirect:/productos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("errorGeneral", ex.getMessage());
            model.addAttribute("tituloFormulario", "Agregar Producto");
            model.addAttribute("accionFormulario", "/productos");
            model.addAttribute("mostrarCodigo", false);
            return "productos/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("productoForm", productoService.convertirAForm(id));
        model.addAttribute("productoId", id);
        model.addAttribute("codigoGenerado", productoService.buscarCodigo(id));
        model.addAttribute("tituloFormulario", "Editar Producto");
        model.addAttribute("accionFormulario", "/productos/" + id);
        model.addAttribute("mostrarCodigo", true);
        return "productos/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("productoForm") ProductoForm productoForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productoId", id);
            model.addAttribute("codigoGenerado", productoService.buscarCodigo(id));
            model.addAttribute("tituloFormulario", "Editar Producto");
            model.addAttribute("accionFormulario", "/productos/" + id);
            model.addAttribute("mostrarCodigo", true);
            return "productos/formulario";
        }

        try {
            productoService.actualizar(id, productoForm);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto actualizado correctamente");
            return "redirect:/productos";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("errorGeneral", ex.getMessage());
            model.addAttribute("productoId", id);
            model.addAttribute("codigoGenerado", productoService.buscarCodigo(id));
            model.addAttribute("tituloFormulario", "Editar Producto");
            model.addAttribute("accionFormulario", "/productos/" + id);
            model.addAttribute("mostrarCodigo", true);
            return "productos/formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto eliminado correctamente");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/productos";
    }
}
