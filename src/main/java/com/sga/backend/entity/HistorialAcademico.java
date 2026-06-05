package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "historialacademico")
public class HistorialAcademico {

    @Id
    @Column(name = "idHistorial", length = 15)
    private String idHistorial;

    @Column(name = "promedioAcumulado", columnDefinition = "DECIMAL(4,2)")
    private java.math.BigDecimal promedioAcumulado = java.math.BigDecimal.ZERO;

    @Column(name = "creditosAprobados")
    private Integer creditosAprobados = 0;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;
}