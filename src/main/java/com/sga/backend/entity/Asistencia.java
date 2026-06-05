package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "asistencia")
public class Asistencia {

    @Id
    @Column(name = "idAsistencia", length = 15)
    private String idAsistencia;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad", nullable = false)
    private Modalidad modalidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAsistencia estado;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    @Column(name = "idSeccion", length = 15, nullable = false)
    private String idSeccion;

    @Column(name = "idDocente", length = 15, nullable = false)
    private String idDocente;

    public enum Modalidad { PRESENCIAL, VIRTUAL }
    public enum EstadoAsistencia { PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO }
}