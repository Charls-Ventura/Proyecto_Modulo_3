package com.factoria.gestionproductos.controller;

import com.factoria.gestionproductos.dto.UnidadMedidaForm;
import com.factoria.gestionproductos.exception.ReglaNegocioException;
import com.factoria.gestionproductos.service.UnidadMedidaService;
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

@Controller
@RequestMapping("/unidades-medida")
public class UnidadMedidaController {

    private final UnidadMedidaService unidadMedidaService;

    public UnidadMedidaController(UnidadMedidaService unidadMedidaService) {
        this.unidadMedidaService = unidadMedidaService;
    }


    @ModelAttribute("seccionActiva")
    public String seccionActiva() {
        return "unidades";
    }
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("unidades", unidadMedidaService.listarTodas());
        return "unidades/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("unidadMedidaForm", new UnidadMedidaForm());
        model.addAttribute("tituloFormulario", "Agregar Unidad de Medida");
        model.addAttribute("accionFormulario", "/unidades-medida");
        return "unidades/formulario";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("unidadMedidaForm") UnidadMedidaForm form,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tituloFormulario", "Agregar Unidad de Medida");
            model.addAttribute("accionFormulario", "/unidades-medida");
            return "unidades/formulario";
        }

        try {
            unidadMedidaService.crear(form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Unidad de medida registrada correctamente");
            return "redirect:/unidades-medida";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("errorGeneral", ex.getMessage());
            model.addAttribute("tituloFormulario", "Agregar Unidad de Medida");
            model.addAttribute("accionFormulario", "/unidades-medida");
            return "unidades/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("unidadMedidaForm", unidadMedidaService.convertirAForm(id));
        model.addAttribute("tituloFormulario", "Editar Unidad de Medida");
        model.addAttribute("accionFormulario", "/unidades-medida/" + id);
        model.addAttribute("unidadId", id);
        return "unidades/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("unidadMedidaForm") UnidadMedidaForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tituloFormulario", "Editar Unidad de Medida");
            model.addAttribute("accionFormulario", "/unidades-medida/" + id);
            model.addAttribute("unidadId", id);
            return "unidades/formulario";
        }

        try {
            unidadMedidaService.actualizar(id, form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Unidad de medida actualizada correctamente");
            return "redirect:/unidades-medida";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("errorGeneral", ex.getMessage());
            model.addAttribute("tituloFormulario", "Editar Unidad de Medida");
            model.addAttribute("accionFormulario", "/unidades-medida/" + id);
            model.addAttribute("unidadId", id);
            return "unidades/formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        unidadMedidaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Unidad de medida eliminada correctamente");
        return "redirect:/unidades-medida";
    }
}
