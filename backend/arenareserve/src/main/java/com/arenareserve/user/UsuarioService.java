package com.arenareserve.user;

import com.arenareserve.auth.RegisterRequest;
import com.arenareserve.enums.Rol;
import com.arenareserve.exception.OperacionNoPermitidaException;
import com.arenareserve.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioSistema loadUserByUsername(String username) {
        return repository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public List<UsuarioSistema> listar() {
        return repository.findAll();
    }

    public UsuarioSistema obtener(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public UsuarioSistema crear(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new OperacionNoPermitidaException("El email del usuario ya existe");
        }
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(request.rol() == null ? Rol.EMPLEADO : request.rol());
        return repository.save(usuario);
    }

    public UsuarioSistema actualizar(Long id, RegisterRequest request) {
        UsuarioSistema usuario = obtener(id);
        repository.findByEmail(request.email())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> { throw new OperacionNoPermitidaException("El email del usuario ya existe"); });
        usuario.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }
        usuario.setRol(request.rol());
        return repository.save(usuario);
    }

    public UsuarioSistema cambiarEstado(Long id) {
        UsuarioSistema usuario = obtener(id);
        if (usuario.isActivo()) usuario.desactivar(); else usuario.activar();
        return repository.save(usuario);
    }

    public boolean noHayUsuarios() {
        return repository.count() == 0;
    }
}
