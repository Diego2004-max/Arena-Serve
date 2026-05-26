package com.arenareserve.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(@NotBlank String nombre, String telefono, @Email @NotBlank String email,
                             @NotBlank String documento, String direccion) {
}
