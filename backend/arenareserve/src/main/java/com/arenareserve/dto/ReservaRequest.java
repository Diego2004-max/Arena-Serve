package com.arenareserve.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record ReservaRequest(@NotNull Long clienteId, @NotNull Long canchaId, @NotNull @FutureOrPresent LocalDate fecha,
                             @NotNull LocalTime horaInicio, @NotNull LocalTime horaFin, Set<Long> serviciosAdicionalesIds) {
}
