package com.factoria.gestionproductos.controller;

import com.factoria.gestionproductos.exception.ReglaNegocioException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String manejarNoEncontrado(EntityNotFoundException ex, Model model) {
        model.addAttribute("titulo", "Recurso no encontrado");
        model.addAttribute("mensaje", ex.getMessage());
        return "error-generico";
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String manejarReglaNegocio(ReglaNegocioException ex, Model model) {
        model.addAttribute("titulo", "No se pudo completar la operación");
        model.addAttribute("mensaje", ex.getMessage());
        return "error-generico";
    }

    @ExceptionHandler(Exception.class)
    public String manejarErrorGeneral(Exception ex, Model model) {
        model.addAttribute("titulo", "Ocurrió un error inesperado");
        model.addAttribute("mensaje", ex.getMessage());
        return "error-generico";
    }
}
