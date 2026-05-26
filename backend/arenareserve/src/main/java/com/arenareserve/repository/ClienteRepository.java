package com.arenareserve.repository;

import com.arenareserve.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByDocumento(String documento);
    Optional<Cliente> findByDocumento(String documento);
}
