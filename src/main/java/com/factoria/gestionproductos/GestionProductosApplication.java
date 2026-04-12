package com.factoria.gestionproductos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestionProductosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionProductosApplication.class, args);
        System.out.println("Aplicacion iniciada en http://localhost:8080/login");
    }

}