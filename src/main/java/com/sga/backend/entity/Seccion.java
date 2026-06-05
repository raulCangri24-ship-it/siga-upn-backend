package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "seccion")
public class Seccion {

    @Id
    @Column(name = "idSeccion", length = 15)
    private String idSeccion;

    @Column(name = "codigo", length = 20, nullable = false)
    private String codigo;

    @Column(name = "capacidadMaxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(name = "matriculados", nullable = false)
    private Integer matriculados = 0;

    @Column(name = "horario", length = 100)
    private String horario;

    @Column(name = "idCurso", length = 10, nullable = false)
    private String idCurso;

    @Column(name = "idDocente", length = 15, nullable = false)
    private String idDocente;

    @Column(name = "idAula", length = 10)
    private String idAula;

    @Column(name = "idPeriodo", length = 10, nullable = false)
    private String idPeriodo;
}