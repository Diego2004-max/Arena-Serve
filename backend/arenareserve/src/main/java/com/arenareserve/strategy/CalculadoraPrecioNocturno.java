package com.arenareserve.strategy;

import com.arenareserve.model.Reserva;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculadoraPrecioNocturno implements CalculadoraPrecio {
    @Override
    public BigDecimal calcular(Reserva reserva) {
        return reserva.getCancha().getPrecioHora()
                .multiply(BigDecimal.valueOf(reserva.calcularDuracion()))
                .multiply(new BigDecimal("1.20"));
    }
}
