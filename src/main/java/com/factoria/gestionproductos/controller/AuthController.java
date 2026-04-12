package com.factoria.gestionproductos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "redirect:/productos";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/productos";
    }
}
