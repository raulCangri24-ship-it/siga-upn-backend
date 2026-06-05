package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "nota")
public class Nota {

    @Id
    @Column(name = "idNota", length = 15)
    private String idNota;

    @Column(name = "valor", precision = 4, scale = 2)
    private BigDecimal valor;

    @Column(name = "fechaRegistro")
    private LocalDateTime fechaRegistro;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    @Column(name = "idEvaluacion", length = 15, nullable = false)
    private String idEvaluacion;
}
