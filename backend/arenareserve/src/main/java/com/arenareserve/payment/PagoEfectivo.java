package com.arenareserve.payment;

import com.arenareserve.model.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoEfectivo implements MetodoPago {
    @Override
    public void procesar(Pago pago) {
        pago.validarValor();
    }
}
