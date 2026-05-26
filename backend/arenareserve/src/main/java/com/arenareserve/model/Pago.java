package com.arenareserve.model;

import com.arenareserve.enums.TipoPago;
import com.arenareserve.exception.PagoInvalidoException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)//asosiado a una reserva 
    private Reserva reserva;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)//guarda como texto 
    private TipoPago tipoPago;

    private LocalDateTime fechaPago = LocalDateTime.now();
    private String referencia;

    public void validarValor() {
        if (valor == null || valor.signum() <= 0) {
            throw new PagoInvalidoException("El valor del pago debe ser mayor a cero");
        }
    }
}
