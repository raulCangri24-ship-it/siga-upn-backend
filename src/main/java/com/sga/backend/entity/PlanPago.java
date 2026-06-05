package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "planpago")
public class PlanPago {

    @Id
    @Column(name = "idPlan", length = 15)
    private String idPlan;

    @Column(name = "numeroCuotas", nullable = false)
    private Integer numeroCuotas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPlan estado = EstadoPlan.VIGENTE;

    @Column(name = "fechaInicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "idDeuda", length = 15, nullable = false)
    private String idDeuda;

    public enum EstadoPlan {
        VIGENTE, INCUMPLIDO, COMPLETADO
    }
}
