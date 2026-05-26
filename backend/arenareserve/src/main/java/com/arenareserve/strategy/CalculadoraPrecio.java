package com.arenareserve.strategy;

import com.arenareserve.model.Reserva;

import java.math.BigDecimal;

public interface CalculadoraPrecio {
    BigDecimal calcular(Reserva reserva);
}
