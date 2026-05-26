package com.arenareserve.controller;

import com.arenareserve.dto.PagoRequest;
import com.arenareserve.dto.PagoResponse;
import com.arenareserve.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService service;

    @PostMapping
    public PagoResponse registrar(@Valid @RequestBody PagoRequest request) { return service.registrar(request); }

    @GetMapping("/reserva/{reservaId}")
    public List<PagoResponse> porReserva(@PathVariable Long reservaId) { return service.porReserva(reservaId); }
}
