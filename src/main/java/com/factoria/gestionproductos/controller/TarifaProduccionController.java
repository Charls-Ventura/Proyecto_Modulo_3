package com.factoria.gestionproductos.controller;

import com.factoria.gestionproductos.domain.Producto;
import com.factoria.gestionproductos.domain.TarifaProduccion;
import com.factoria.gestionproductos.dto.TarifaProduccionForm;
import com.factoria.gestionproductos.service.ProductoService;
import com.factoria.gestionproductos.service.TarifaProduccionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tarifas-produccion")
public class TarifaProduccionController {

    private final ProductoService productoService;
    private final TarifaProduccionService tarifaProduccionService;

    public TarifaProduccionController(ProductoService productoService,
                                      TarifaProduccionService tarifaProduccionService) {
        this.productoService = productoService;
        this.tarifaProduccionService = tarifaProduccionService;
    }


    @ModelAttribute("seccionActiva")
    public String seccionActiva() {
        return "tarifas";
    }
    @GetMapping
    public String listar(Model model) {
        List<Producto> productos = productoService.listar("", 0, 200).getContent();
        Map<Long, BigDecimal> tarifasActuales = new HashMap<>();
        for (Producto producto : productos) {
            tarifasActuales.put(producto.getId(), tarifaProduccionService.buscarMontoActual(producto.getId()));
        }
        model.addAttribute("productos", productos);
        model.addAttribute("tarifasActuales", tarifasActuales);
        return "tarifas/lista";
    }

    @GetMapping("/producto/{id}/cambiar")
    public String cambiar(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        TarifaProduccion actual = tarifaProduccionService.buscarTarifaActual(id);

        TarifaProduccionForm form = new TarifaProduccionForm();
        if (actual != null) {
            form.setMonto(actual.getMonto());
        }

        model.addAttribute("producto", producto);
        model.addAttribute("tarifaActual", actual);
        model.addAttribute("tarifaForm", form);
        return "tarifas/formulario";
    }

    @PostMapping("/producto/{id}/cambiar")
    public String guardarCambio(@PathVariable Long id,
                                @Valid @ModelAttribute("tarifaForm") TarifaProduccionForm tarifaForm,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Producto producto = productoService.buscarPorId(id);
        TarifaProduccion actual = tarifaProduccionService.buscarTarifaActual(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("producto", producto);
            model.addAttribute("tarifaActual", actual);
            return "tarifas/formulario";
        }

        tarifaProduccionService.cambiarTarifa(producto, tarifaForm);
        redirectAttributes.addFlashAttribute("mensajeExito", "Tarifa de producción actualizada correctamente");
        return "redirect:/productos";
    }
}
