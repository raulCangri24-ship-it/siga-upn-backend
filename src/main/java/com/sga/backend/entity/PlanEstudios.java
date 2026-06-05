package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "planestudios")
public class PlanEstudios {

    @Id
    @Column(name = "idPlan", length = 10)
    private String idPlan;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "carrera", length = 150, nullable = false)
    private String carrera;

    @Column(name = "anioVigencia", nullable = false)
    private Integer anioVigencia;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
}