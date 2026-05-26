package com.arenareserve.service;

import com.arenareserve.dto.DisponibilidadResponse;
import com.arenareserve.exception.HorarioNoDisponibleException;
import com.arenareserve.model.Cancha;
import com.arenareserve.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilidadService {
    private static final LocalTime APERTURA = LocalTime.of(8, 0);
    private static final LocalTime CIERRE = LocalTime.of(22, 0);

    private final CanchaService canchaService;
    private final ReservaRepository reservaRepository;

    public List<DisponibilidadResponse> consultar(Long canchaId, LocalDate fecha) {
        Cancha cancha = canchaService.find(canchaId);
        if (!cancha.estaActiva()) {
            throw new HorarioNoDisponibleException("La cancha no esta activa");
        }
        List<DisponibilidadResponse> franjas = new ArrayList<>();
        generarRecursivo(canchaId, fecha, APERTURA, CIERRE, franjas);
        return franjas;
    }

    private void generarRecursivo(Long canchaId, LocalDate fecha, LocalTime actual, LocalTime cierre,
                                  List<DisponibilidadResponse> franjas) {
        if (!actual.plusHours(1).isAfter(cierre)) {
            LocalTime fin = actual.plusHours(1);
            boolean disponible = !reservaRepository.existeSolapamiento(canchaId, fecha, actual, fin);
            franjas.add(new DisponibilidadResponse(actual, fin, disponible));
            generarRecursivo(canchaId, fecha, fin, cierre, franjas);
        }
    }
}
