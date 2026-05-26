package com.arenareserve.service;

import com.arenareserve.enums.EstadoCancha;
import com.arenareserve.dto.ReservaRequest;
import com.arenareserve.dto.ReservaResponse;
import com.arenareserve.enums.EstadoReserva;
import com.arenareserve.exception.HorarioNoDisponibleException;
import com.arenareserve.exception.OperacionNoPermitidaException;
import com.arenareserve.exception.ResourceNotFoundException;
import com.arenareserve.exception.ReservaSolapadaException;
import com.arenareserve.model.Cancha;
import com.arenareserve.model.Reserva;
import com.arenareserve.model.ServicioAdicional;
import com.arenareserve.repository.ReservaRepository;
import com.arenareserve.repository.ServicioAdicionalRepository;
import com.arenareserve.strategy.CalculadoraPrecioFinSemana;
import com.arenareserve.strategy.CalculadoraPrecioNocturno;
import com.arenareserve.strategy.CalculadoraPrecioNormal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository repository;
    private final ClienteService clienteService;
    private final CanchaService canchaService;
    private final ServicioAdicionalRepository servicioRepository;
    private final CalculadoraPrecioNormal precioNormal;
    private final CalculadoraPrecioNocturno precioNocturno;
    private final CalculadoraPrecioFinSemana precioFinSemana;

    @Transactional(readOnly = true)
    public List<ReservaResponse> listar() {
        return repository.findAllConDetalle().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtener(Long id) {
        return toResponse(findConDetalle(id));
    }

    @Transactional
    public ReservaResponse crear(ReservaRequest request) {
        Cancha cancha = canchaService.find(request.canchaId());
        validarCanchaActiva(cancha);
        Reserva reserva = new Reserva();
        reserva.setCliente(clienteService.find(request.clienteId()));
        reserva.setCancha(cancha);
        reserva.setFecha(request.fecha());
        reserva.setHoraInicio(request.horaInicio());
        reserva.setHoraFin(request.horaFin());
        reserva.validarHorario();
        if (repository.existeSolapamiento(cancha.getId(), reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin())) {
            throw new ReservaSolapadaException("La cancha ya tiene una reserva en ese horario");
        }
        reserva.setServiciosAdicionales(cargarServicios(request.serviciosAdicionalesIds()));
        reserva.asignarTotal(calcularTotal(reserva));
        return toResponse(repository.save(reserva));
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = find(id);
        reserva.cancelar();
        return toResponse(repository.save(reserva));
    }

    @Transactional
    public ReservaResponse finalizar(Long id) {
        Reserva reserva = find(id);
        reserva.finalizar();
        return toResponse(repository.save(reserva));
    }

    public Reserva find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
    }

    private Reserva findConDetalle(Long id) {
        return repository.findByIdConDetalle(id).orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
    }

    private void validarCanchaActiva(Cancha cancha) {
        if (EstadoCancha.ACTIVA.equals(cancha.getEstadoCancha())) {
            return;
        }
        if (EstadoCancha.INACTIVA.equals(cancha.getEstadoCancha())) {
            throw new OperacionNoPermitidaException("No se puede reservar esta cancha porque está inactiva");
        }
        if (EstadoCancha.MANTENIMIENTO.equals(cancha.getEstadoCancha())) {
            throw new OperacionNoPermitidaException("No se puede reservar esta cancha porque está en mantenimiento");
        }
        throw new OperacionNoPermitidaException("No se puede reservar esta cancha porque no está activa");
    }

    private Set<ServicioAdicional> cargarServicios(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return ids.stream()
                .map(id -> servicioRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Servicio adicional no encontrado")))
                .collect(Collectors.toSet());
    }

    private BigDecimal calcularTotal(Reserva reserva) {
        BigDecimal base = esFinSemana(reserva) ? precioFinSemana.calcular(reserva)
                : reserva.getHoraInicio().getHour() >= 18 ? precioNocturno.calcular(reserva) : precioNormal.calcular(reserva);
        BigDecimal extras = reserva.getServiciosAdicionales().stream()
                .map(ServicioAdicional::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return base.add(extras);
    }

    private boolean esFinSemana(Reserva reserva) {
        DayOfWeek day = reserva.getFecha().getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getCliente().getId(), reserva.getCliente().getNombre(),
                reserva.getCancha().getId(), reserva.getCancha().getNombre(), reserva.getFecha(),
                reserva.getHoraInicio(), reserva.getHoraFin(), reserva.getEstadoReserva(), reserva.getTotal(),
                reserva.getFechaCreacion(), reserva.getFechaCancelacion(),
                reserva.getServiciosAdicionales().stream().map(ServicioAdicional::getNombre).collect(Collectors.toSet()));
    }
}
