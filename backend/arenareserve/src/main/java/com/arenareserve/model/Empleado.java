package com.arenareserve.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Empleado extends Persona {
    private String cargo;
    private LocalDate fechaContratacion = LocalDate.now();
    private boolean activo = true;
}
