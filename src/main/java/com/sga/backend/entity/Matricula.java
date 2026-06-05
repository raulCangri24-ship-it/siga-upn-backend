package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "matricula")
public class Matricula {

    @Id
    @Column(name = "idMatricula", length = 15)
    private String idMatricula;

    @Column(name = "fecha")
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoMatricula estado = EstadoMatricula.PENDIENTE;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    @Column(name = "idSeccion", length = 15, nullable = false)
    private String idSeccion;

    @Column(name = "idPeriodo", length = 10, nullable = false)
    private String idPeriodo;

    public enum EstadoMatricula {
        CONFIRMADA, CANCELADA, PENDIENTE
    }
}