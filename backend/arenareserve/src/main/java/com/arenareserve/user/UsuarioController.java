package com.arenareserve.user;

import com.arenareserve.auth.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @GetMapping
    public List<UsuarioSistema> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public UsuarioSistema obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    public UsuarioSistema crear(@Valid @RequestBody RegisterRequest request) { return service.crear(request); }

    @PutMapping("/{id}")
    public UsuarioSistema actualizar(@PathVariable Long id, @Valid @RequestBody RegisterRequest request) {
        return service.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public UsuarioSistema estado(@PathVariable Long id) { return service.cambiarEstado(id); }
}
