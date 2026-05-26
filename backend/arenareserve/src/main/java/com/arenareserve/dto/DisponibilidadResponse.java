package com.arenareserve.dto;

import java.time.LocalTime;

public record DisponibilidadResponse(LocalTime horaInicio, LocalTime horaFin, boolean disponible) {
}
