package com.arenareserve.service;

import com.arenareserve.dto.DashboardResponse;
import com.arenareserve.enums.EstadoReserva;
import com.arenareserve.repository.PagoRepository;
import com.arenareserve.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final ReservaService reservaService;

    @Transactional(readOnly = true)
    public DashboardResponse obtener() {
        LocalDate hoy = LocalDate.now();
        return new DashboardResponse(
                reservaRepository.countByFecha(hoy),
                reservaRepository.countByEstadoReserva(EstadoReserva.PENDIENTE),
                reservaRepository.countByEstadoReserva(EstadoReserva.CONFIRMADA),
                pagoRepository.ingresosEntre(hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay().minusNanos(1)),
                reservaRepository.findTop5ByFechaGreaterThanEqualAndEstadoReservaNotOrderByFechaAscHoraInicioAsc(hoy, EstadoReserva.CANCELADA)
                        .stream().map(reservaService::toResponse).toList()
        );
    }
}
