package com.arenareserve.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(long reservasHoy, long reservasPendientes, long reservasConfirmadas,
                                BigDecimal ingresosDelDia, List<ReservaResponse> proximasReservas) {
}
