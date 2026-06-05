package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "evaluacion")
public class Evaluacion {

    @Id
    @Column(name = "idEvaluacion", length = 15)
    private String idEvaluacion;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoEvaluacion tipo;

    @Column(name = "peso", precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(name = "idSilabo", length = 15, nullable = false)
    private String idSilabo;

    public enum TipoEvaluacion {
        PRACTICA, EXAMEN, TRABAJO, LABORATORIO
    }
}
