package com.arenareserve.payment;

import com.arenareserve.model.Pago;

public interface MetodoPago {
    void procesar(Pago pago);
}
