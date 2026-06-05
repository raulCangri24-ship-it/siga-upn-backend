package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @Column(name = "idPago", length = 15)
    private String idPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "concepto", length = 150)
    private String concepto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPago estado = EstadoPago.PROCESANDO;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    @Column(name = "idDeuda", length = 15, nullable = false)
    private String idDeuda;

    public enum EstadoPago {
        CONFIRMADO, ANULADO, PROCESANDO
    }
}
