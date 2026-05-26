package com.arenareserve.auth;

import com.arenareserve.exception.OperacionNoPermitidaException;
import com.arenareserve.user.UsuarioService;
import com.arenareserve.user.UsuarioSistema;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UsuarioSistema usuario = usuarioService.loadUserByUsername(request.email());
        return response(usuario);
    }

    public AuthResponse register(RegisterRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean adminAutenticado = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!usuarioService.noHayUsuarios() && !adminAutenticado) {
            throw new OperacionNoPermitidaException("Solo ADMIN puede registrar usuarios");
        }
        return response(usuarioService.crear(request));
    }

    public AuthResponse me(String email) {
        return response(usuarioService.loadUserByUsername(email));
    }

    private AuthResponse response(UsuarioSistema usuario) {
        return new AuthResponse(jwtService.generarToken(usuario), usuario.getId(), usuario.getEmail(), usuario.getRol());
    }
}
