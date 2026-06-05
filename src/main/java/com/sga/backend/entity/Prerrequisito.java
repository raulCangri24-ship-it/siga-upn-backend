package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "prerrequisito")
public class Prerrequisito {

    @Id
    @Column(name = "idPrerrequisito", length = 15)
    private String idPrerrequisito;

    @Column(name = "idCurso", length = 10, nullable = false)
    private String idCurso;

    @Column(name = "idCursoRequerido", length = 10, nullable = false)
    private String idCursoRequerido;
}