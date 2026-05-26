package com.arenareserve.dto;

import com.arenareserve.enums.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record ReservaResponse(Long id, Long clienteId, String clienteNombre, Long canchaId, String canchaNombre,
                              LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoReserva estadoReserva,
                              BigDecimal total, LocalDateTime fechaCreacion, LocalDateTime fechaCancelacion,
                              Set<String> serviciosAdicionales) {
}
