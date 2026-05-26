package com.arenareserve.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioSistema, Long> {
    Optional<UsuarioSistema> findByEmail(String email);
    boolean existsByEmail(String email);
}
