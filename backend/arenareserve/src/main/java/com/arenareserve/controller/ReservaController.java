package com.arenareserve.controller;

import com.arenareserve.dto.ReservaRequest;
import com.arenareserve.dto.ReservaResponse;
import com.arenareserve.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService service;

    @GetMapping
    public List<ReservaResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ReservaResponse obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    public ReservaResponse crear(@Valid @RequestBody ReservaRequest request) { return service.crear(request); }

    @PatchMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id) { return service.cancelar(id); }

    @PatchMapping("/{id}/finalizar")
    public ReservaResponse finalizar(@PathVariable Long id) { return service.finalizar(id); }
}
