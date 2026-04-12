package com.factoria.gestionproductos.dto;

import com.factoria.gestionproductos.domain.TipoProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductoForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private TipoProducto tipo;

    @NotNull(message = "La tarifa de producción es obligatoria")
    @DecimalMin(value = "0.01", inclusive = true, message = "La tarifa de producción debe ser mayor que cero")
    private BigDecimal tarifaProduccion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProducto tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getTarifaProduccion() {
        return tarifaProduccion;
    }

    public void setTarifaProduccion(BigDecimal tarifaProduccion) {
        this.tarifaProduccion = tarifaProduccion;
    }
}
