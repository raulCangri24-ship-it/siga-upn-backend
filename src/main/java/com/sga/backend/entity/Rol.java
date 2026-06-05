package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @Column(name = "idRol", length = 10)
    private String idRol;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true)
    private NombreRol nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public enum NombreRol {
        ADMIN, COORDINADOR, DOCENTE, ESTUDIANTE, RECTOR
    }
}
