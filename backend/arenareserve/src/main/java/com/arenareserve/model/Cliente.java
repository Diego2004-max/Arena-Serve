package com.arenareserve.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cliente extends Persona {

    private String direccion;
    private LocalDate fechaRegistro = LocalDate.now();
    private boolean activo = true;

    @OneToMany(mappedBy = "cliente")//controla relacion con reserva
    private List<Reserva> reservas = new ArrayList<>();

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }
}
