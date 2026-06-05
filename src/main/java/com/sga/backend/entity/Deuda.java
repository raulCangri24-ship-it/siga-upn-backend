package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "deuda")
public class Deuda {

    @Id
    @Column(name = "idDeuda", length = 15)
    private String idDeuda;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "concepto", nullable = false, length = 150)
    private String concepto;

    @Column(name = "fechaVencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDeuda estado = EstadoDeuda.PENDIENTE;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    public enum EstadoDeuda {
        PENDIENTE, PAGADA, VENCIDA
    }
}
