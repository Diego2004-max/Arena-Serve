package com.arenareserve.dto;

import java.time.LocalDate;

public record ClienteResponse(Long id, String nombre, String telefono, String email, String documento,
                              String direccion, LocalDate fechaRegistro, boolean activo) {
}
