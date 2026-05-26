package com.arenareserve.repository;

import com.arenareserve.model.ServicioAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Long> {
    boolean existsByNombre(String nombre);
}
