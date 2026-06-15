package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "convalidacion")
public class Convalidacion {

    @Id
    @Column(name = "idConvalidacion", length = 15)
    private String idConvalidacion;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    @Column(name = "idCurso", length = 10, nullable = false)
    private String idCurso;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoConvalidacion estado;

    public enum EstadoConvalidacion {
        PENDIENTE, APROBADA, RECHAZADA
    }
}
