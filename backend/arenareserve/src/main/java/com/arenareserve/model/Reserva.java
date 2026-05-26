package com.arenareserve.model;

import com.arenareserve.enums.EstadoReserva;
import com.arenareserve.exception.OperacionNoPermitidaException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)//reserva debe tener un cliente
    private Cliente cliente;

    @ManyToOne(optional = false)
    private Cancha cancha;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;//por defecto es pendiente

    private BigDecimal total = BigDecimal.ZERO;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private LocalDateTime fechaCancelacion;

    @ManyToMany
    @JoinTable(name = "reserva_servicio",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id"))
    private Set<ServicioAdicional> serviciosAdicionales = new HashSet<>();

    public long calcularDuracion() {
        validarHorario();
        return Duration.between(horaInicio, horaFin).toHours();
    }

    public void confirmar() {
        if (EstadoReserva.CANCELADA.equals(estadoReserva)) {
            throw new OperacionNoPermitidaException("Una reserva cancelada no puede confirmarse");
        }
        this.estadoReserva = EstadoReserva.CONFIRMADA;
    }

    public void cancelar() {
        if (EstadoReserva.FINALIZADA.equals(estadoReserva)) {
            throw new OperacionNoPermitidaException("Una reserva finalizada no puede cancelarse");
        }
        this.estadoReserva = EstadoReserva.CANCELADA;
        this.fechaCancelacion = LocalDateTime.now();
    }

    public void finalizar() {
        if (EstadoReserva.CANCELADA.equals(estadoReserva)) {
            throw new OperacionNoPermitidaException("Una reserva cancelada no puede finalizarse");
        }
        this.estadoReserva = EstadoReserva.FINALIZADA;
    }

    public void asignarTotal(BigDecimal total) {
        if (total == null || total.signum() < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo");
        }
        this.total = total;
    }

    public void validarHorario() {
        if (fecha == null || horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("Fecha y horas son obligatorias");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a la actual");
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora fin debe ser mayor que la hora inicio");
        }
    }
}
