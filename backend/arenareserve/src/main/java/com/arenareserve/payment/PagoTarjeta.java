package com.arenareserve.payment;

import com.arenareserve.exception.PagoInvalidoException;
import com.arenareserve.model.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoTarjeta implements MetodoPago {
    @Override
    public void procesar(Pago pago) {
        pago.validarValor();
        if (pago.getReferencia() == null || pago.getReferencia().length() < 4) {
            throw new PagoInvalidoException("El pago con tarjeta requiere referencia valida");
        }
    }
}
