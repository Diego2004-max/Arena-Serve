package com.arenareserve.dto;

import com.arenareserve.enums.EstadoCancha;
import com.arenareserve.enums.TipoCancha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CanchaRequest(@NotBlank String nombre, @NotNull TipoCancha tipoCancha, @Positive int capacidad,
                            @NotNull @Positive BigDecimal precioHora, EstadoCancha estadoCancha) {
}
