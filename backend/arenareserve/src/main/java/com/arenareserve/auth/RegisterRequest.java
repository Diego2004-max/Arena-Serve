package com.arenareserve.auth;

import com.arenareserve.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min = 8) String password,
                              @NotNull Rol rol, Long empleadoId) {
}
