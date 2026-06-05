package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "idUsuario", length = 15)
    private String idUsuario;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true, length = 120)
    private String correo;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idRol", nullable = false)
    private Rol rol;

    @Column(name = "fechaCreacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "intentosFallidos")
    private Integer intentosFallidos = 0;

    public enum EstadoUsuario {
        ACTIVO, INACTIVO, BLOQUEADO
    }
}
