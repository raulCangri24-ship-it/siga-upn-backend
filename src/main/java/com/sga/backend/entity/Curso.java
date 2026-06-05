package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @Column(name = "idCurso", length = 10)
    private String idCurso;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "creditos", nullable = false)
    private Integer creditos;

    @Column(name = "ciclo", nullable = false)
    private Integer ciclo;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad", nullable = false)
    private Modalidad modalidad;

    @Column(name = "idPlan", length = 10, nullable = false)
    private String idPlan;

    public enum Modalidad {
        PRESENCIAL, SEMIPRESENCIAL, VIRTUAL
    }
}