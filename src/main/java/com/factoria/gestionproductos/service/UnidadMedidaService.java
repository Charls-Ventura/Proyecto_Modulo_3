package com.factoria.gestionproductos.service;

import com.factoria.gestionproductos.domain.UnidadMedida;
import com.factoria.gestionproductos.dto.UnidadMedidaForm;
import com.factoria.gestionproductos.exception.ReglaNegocioException;
import com.factoria.gestionproductos.repository.UnidadMedidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    public UnidadMedidaService(UnidadMedidaRepository unidadMedidaRepository) {
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    @Transactional(readOnly = true)
    public List<UnidadMedida> listarTodas() {
        return unidadMedidaRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }

    @Transactional(readOnly = true)
    public UnidadMedida buscarPorId(Long id) {
        return unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la unidad de medida solicitada"));
    }

    @Transactional
    public UnidadMedida crear(UnidadMedidaForm form) {
        validarDuplicados(form, null);
        UnidadMedida unidadMedida = new UnidadMedida();
        mapear(unidadMedida, form);
        return unidadMedidaRepository.save(unidadMedida);
    }

    @Transactional
    public UnidadMedida actualizar(Long id, UnidadMedidaForm form) {
        UnidadMedida unidadMedida = buscarPorId(id);
        validarDuplicados(form, id);
        mapear(unidadMedida, form);
        return unidadMedidaRepository.save(unidadMedida);
    }

    @Transactional
    public void eliminar(Long id) {
        UnidadMedida unidadMedida = buscarPorId(id);
        unidadMedidaRepository.delete(unidadMedida);
    }

    @Transactional(readOnly = true)
    public UnidadMedidaForm convertirAForm(Long id) {
        UnidadMedida unidadMedida = buscarPorId(id);
        UnidadMedidaForm form = new UnidadMedidaForm();
        form.setNombre(unidadMedida.getNombre());
        form.setAbreviatura(unidadMedida.getAbreviatura());
        form.setActivo(unidadMedida.getActivo());
        return form;
    }

    private void validarDuplicados(UnidadMedidaForm form, Long idActual) {
        String nombre = form.getNombre().trim();
        String abreviatura = form.getAbreviatura().trim();

        boolean nombreDuplicado = unidadMedidaRepository.findAll().stream()
                .anyMatch(unidad -> unidad.getNombre().equalsIgnoreCase(nombre)
                        && (idActual == null || !unidad.getId().equals(idActual)));

        boolean abreviaturaDuplicada = unidadMedidaRepository.findAll().stream()
                .anyMatch(unidad -> unidad.getAbreviatura().equalsIgnoreCase(abreviatura)
                        && (idActual == null || !unidad.getId().equals(idActual)));

        if (nombreDuplicado) {
            throw new ReglaNegocioException("Ya existe una unidad de medida con ese nombre");
        }
        if (abreviaturaDuplicada) {
            throw new ReglaNegocioException("Ya existe una unidad de medida con esa abreviatura");
        }
    }

    private void mapear(UnidadMedida unidadMedida, UnidadMedidaForm form) {
        unidadMedida.setNombre(form.getNombre().trim());
        unidadMedida.setAbreviatura(form.getAbreviatura().trim());
        unidadMedida.setActivo(form.getActivo());
    }
}
