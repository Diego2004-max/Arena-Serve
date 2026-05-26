package com.arenareserve.dto;

import com.arenareserve.enums.EstadoCancha;
import com.arenareserve.enums.TipoCancha;

import java.math.BigDecimal;

public record CanchaResponse(Long id, String nombre, TipoCancha tipoCancha, int capacidad,
                             BigDecimal precioHora, EstadoCancha estadoCancha) {
}
