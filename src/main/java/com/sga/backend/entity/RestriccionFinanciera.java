package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "restriccionfinanciera")
public class RestriccionFinanciera {

    @Id
    @Column(name = "idRestriccion", length = 15)
    private String idRestriccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoRestriccion tipo;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @Column(name = "fechaEmision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fechaLevantamiento")
    private LocalDateTime fechaLevantamiento;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    @Column(name = "idDeuda", length = 15, nullable = false)
    private String idDeuda;

    public enum TipoRestriccion {
        MATRICULA, SERVICIOS, TOTAL
    }
}
