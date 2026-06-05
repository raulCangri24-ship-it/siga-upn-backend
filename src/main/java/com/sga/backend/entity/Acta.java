package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "acta")
public class Acta {

    @Id
    @Column(name = "idActa", length = 15)
    private String idActa;

    @Column(name = "fechaGeneracion")
    private LocalDateTime fechaGeneracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoActa estado;

    @Column(name = "fechaFirma")
    private LocalDateTime fechaFirma;

    @Column(name = "idSeccion", length = 15, nullable = false)
    private String idSeccion;

    @Column(name = "idDocente", length = 15, nullable = false)
    private String idDocente;

    public enum EstadoActa {
        BORRADOR, FIRMADA
    }
}
