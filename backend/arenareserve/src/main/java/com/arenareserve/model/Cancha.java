package com.arenareserve.model;

import com.arenareserve.enums.EstadoCancha;
import com.arenareserve.enums.TipoCancha;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cancha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoCancha tipoCancha;
    private int capacidad;
    private BigDecimal precioHora;

    @Enumerated(EnumType.STRING)
    private EstadoCancha estadoCancha = EstadoCancha.ACTIVA;//defecto es activa

    @OneToMany(mappedBy = "cancha")
    private List<Reserva> reservas = new ArrayList<>();

    public void activar() {
        this.estadoCancha = EstadoCancha.ACTIVA;
    }

    public void desactivar() {
        this.estadoCancha = EstadoCancha.INACTIVA;
    }

    public void ponerEnMantenimiento() {
        this.estadoCancha = EstadoCancha.MANTENIMIENTO;
    }

    public boolean estaActiva() {
        return EstadoCancha.ACTIVA.equals(estadoCancha);
    }

    public void cambiarPrecioHora(BigDecimal precio) {
        if (precio == null || precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        this.precioHora = precio;
    }
}
