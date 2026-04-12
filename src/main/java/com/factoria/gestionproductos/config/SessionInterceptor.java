package com.factoria.gestionproductos.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    public static final String SESSION_USER = "usuarioLogueado";
    public static final String SESSION_ROLE = "rolLogueado";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Sin autenticación — acceso libre
        return true;
    }
}
