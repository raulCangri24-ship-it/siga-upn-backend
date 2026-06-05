package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "aula")
public class Aula {

    @Id
    @Column(name = "idAula", length = 10)
    private String idAula;

    @Column(name = "codigo", length = 20, nullable = false)
    private String codigo;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoAula tipo;

    @Column(name = "aforo", nullable = false)
    private Integer aforo;

    public enum TipoAula {
        FISICA, VIRTUAL, LABORATORIO
    }
}