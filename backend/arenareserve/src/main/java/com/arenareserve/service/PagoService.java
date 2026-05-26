package com.arenareserve.service;

import com.arenareserve.dto.PagoRequest;
import com.arenareserve.dto.PagoResponse;
import com.arenareserve.enums.EstadoReserva;
import com.arenareserve.enums.TipoPago;
import com.arenareserve.exception.OperacionNoPermitidaException;
import com.arenareserve.model.Pago;
import com.arenareserve.model.Reserva;
import com.arenareserve.payment.PagoEfectivo;
import com.arenareserve.payment.PagoTarjeta;
import com.arenareserve.payment.PagoTransferencia;
import com.arenareserve.repository.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository repository;
    private final ReservaService reservaService;
    private final PagoEfectivo efectivo;
    private final PagoTransferencia transferencia;
    private final PagoTarjeta tarjeta;

    @Transactional
    public PagoResponse registrar(PagoRequest request) {
        Reserva reserva = reservaService.find(request.reservaId());
        if (EstadoReserva.CANCELADA.equals(reserva.getEstadoReserva())) {
            throw new OperacionNoPermitidaException("Una reserva cancelada no puede pagarse");
        }
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setValor(request.valor());
        pago.setTipoPago(request.tipoPago());
        pago.setReferencia(request.referencia());
        metodo(request.tipoPago()).procesar(pago);
        reserva.confirmar();
        return toResponse(repository.save(pago));
    }

    public List<PagoResponse> porReserva(Long reservaId) {
        return repository.findByReservaId(reservaId).stream().map(this::toResponse).toList();
    }

    private com.arenareserve.payment.MetodoPago metodo(TipoPago tipoPago) {
        return switch (tipoPago) {
            case EFECTIVO -> efectivo;
            case TRANSFERENCIA -> transferencia;
            case TARJETA -> tarjeta;
        };
    }

    private PagoResponse toResponse(Pago pago) {
        return new PagoResponse(pago.getId(), pago.getReserva().getId(), pago.getValor(), pago.getTipoPago(),
                pago.getFechaPago(), pago.getReferencia());
    }
}
