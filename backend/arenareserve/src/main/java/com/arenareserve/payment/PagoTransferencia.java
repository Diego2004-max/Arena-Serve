package com.arenareserve.payment;

import com.arenareserve.exception.PagoInvalidoException;
import com.arenareserve.model.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoTransferencia implements MetodoPago {
    @Override
    public void procesar(Pago pago) {
        pago.validarValor();
        if (pago.getReferencia() == null || pago.getReferencia().isBlank()) {
            throw new PagoInvalidoException("La transferencia requiere referencia");
        }
    }
}
