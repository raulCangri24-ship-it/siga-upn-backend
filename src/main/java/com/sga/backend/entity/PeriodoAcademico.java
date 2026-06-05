package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "periodoacademico")
public class PeriodoAcademico {

    @Id
    @Column(name = "idPeriodo", length = 10)
    private String idPeriodo;

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "fechaInicio", nullable = false)
    private java.time.LocalDate fechaInicio;

    @Column(name = "fechaFin", nullable = false)
    private java.time.LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPeriodo estado;

    public enum EstadoPeriodo {
        ABIERTO, CERRADO, PROGRAMADO
    }
}