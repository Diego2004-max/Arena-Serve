package com.arenareserve.dto;

import com.arenareserve.enums.TipoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PagoRequest(@NotNull Long reservaId, @NotNull @Positive BigDecimal valor,
                          @NotNull TipoPago tipoPago, String referencia) {
}
