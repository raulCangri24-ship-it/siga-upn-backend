package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "material")
public class Material {

    @Id
    @Column(name = "idMaterial", length = 15)
    private String idMaterial;

    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMaterial tipo;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "fechaPublicacion")
    private LocalDateTime fechaPublicacion;

    @Column(name = "idSeccion", length = 15, nullable = false)
    private String idSeccion;

    @Column(name = "idDocente", length = 15, nullable = false)
    private String idDocente;

    public enum TipoMaterial { DOCUMENTO, VIDEO, ENLACE, PRESENTACION }
}