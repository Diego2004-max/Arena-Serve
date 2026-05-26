package com.arenareserve.service;

import com.arenareserve.dto.CanchaRequest;
import com.arenareserve.dto.CanchaResponse;
import com.arenareserve.enums.EstadoCancha;
import com.arenareserve.exception.ResourceNotFoundException;
import com.arenareserve.model.Cancha;
import com.arenareserve.repository.CanchaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CanchaService {
    private final CanchaRepository repository;

    public List<CanchaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public CanchaResponse obtener(Long id) {
        return toResponse(find(id));
    }

    public CanchaResponse crear(CanchaRequest request) {
        Cancha cancha = new Cancha();
        apply(cancha, request);
        return toResponse(repository.save(cancha));
    }

    public CanchaResponse actualizar(Long id, CanchaRequest request) {
        Cancha cancha = find(id);
        apply(cancha, request);
        return toResponse(repository.save(cancha));
    }

    public CanchaResponse cambiarEstado(Long id, EstadoCancha estado) {
        Cancha cancha = find(id);
        if (EstadoCancha.ACTIVA.equals(estado)) cancha.activar();
        if (EstadoCancha.INACTIVA.equals(estado)) cancha.desactivar();
        if (EstadoCancha.MANTENIMIENTO.equals(estado)) cancha.ponerEnMantenimiento();
        return toResponse(repository.save(cancha));
    }

    public Cancha find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada"));
    }

    private void apply(Cancha cancha, CanchaRequest request) {
        cancha.setNombre(request.nombre());
        cancha.setTipoCancha(request.tipoCancha());
        cancha.setCapacidad(request.capacidad());
        cancha.cambiarPrecioHora(request.precioHora());
        if (request.estadoCancha() != null) {
            cambiarEstadoInterno(cancha, request.estadoCancha());
        }
    }

    private void cambiarEstadoInterno(Cancha cancha, EstadoCancha estado) {
        if (EstadoCancha.ACTIVA.equals(estado)) cancha.activar();
        if (EstadoCancha.INACTIVA.equals(estado)) cancha.desactivar();
        if (EstadoCancha.MANTENIMIENTO.equals(estado)) cancha.ponerEnMantenimiento();
    }

    CanchaResponse toResponse(Cancha cancha) {
        return new CanchaResponse(cancha.getId(), cancha.getNombre(), cancha.getTipoCancha(), cancha.getCapacidad(),
                cancha.getPrecioHora(), cancha.getEstadoCancha());
    }
}
