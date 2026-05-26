package com.arenareserve.auth;

import com.arenareserve.enums.Rol;

public record AuthResponse(String token, Long userId, String email, Rol rol) {
}
