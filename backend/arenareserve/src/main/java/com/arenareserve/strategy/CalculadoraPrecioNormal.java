package com.arenareserve.strategy;

import com.arenareserve.model.Reserva;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculadoraPrecioNormal implements CalculadoraPrecio {
    @Override
    public BigDecimal calcular(Reserva reserva) {
        return reserva.getCancha().getPrecioHora().multiply(BigDecimal.valueOf(reserva.calcularDuracion()));
    }
}
