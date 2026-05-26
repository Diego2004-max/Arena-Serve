package com.arenareserve.controller;

import com.arenareserve.dto.CanchaRequest;
import com.arenareserve.dto.CanchaResponse;
import com.arenareserve.enums.EstadoCancha;
import com.arenareserve.service.CanchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/canchas")
@RequiredArgsConstructor
public class CanchaController {
    private final CanchaService service;

    @GetMapping
    public List<CanchaResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public CanchaResponse obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CanchaResponse crear(@Valid @RequestBody CanchaRequest request) { return service.crear(request); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CanchaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CanchaRequest request) {
        return service.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public CanchaResponse estado(@PathVariable Long id, @RequestParam EstadoCancha estado) {
        return service.cambiarEstado(id, estado);
    }
}
