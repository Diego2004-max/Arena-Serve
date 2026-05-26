package com.arenareserve.dto;

import com.arenareserve.enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(Long id, Long reservaId, BigDecimal valor, TipoPago tipoPago,
                           LocalDateTime fechaPago, String referencia) {
}
