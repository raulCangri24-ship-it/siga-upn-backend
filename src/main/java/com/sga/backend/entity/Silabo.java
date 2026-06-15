package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "silabo")
public class Silabo {

    @Id
    @Column(name = "idSilabo", length = 15)
    private String idSilabo;

    @Column(name = "formulaEvaluacion", length = 255)
    private String formulaEvaluacion;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "publicado")
    private Boolean publicado;

    @Column(name = "fechaPublicacion")
    private LocalDate fechaPublicacion;

    @Column(name = "fechaCierre")
    private LocalDate fechaCierre;

    @Column(name = "idSeccion", length = 15, nullable = false)
    private String idSeccion;

    @Column(name = "idDocente", length = 15, nullable = false)
    private String idDocente;
}
