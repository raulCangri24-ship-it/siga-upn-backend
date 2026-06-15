package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @Column(name = "idNotificacion", length = 20)
    private String idNotificacion;

    @Column(name = "tipo", length = 30, nullable = false)
    private String tipo;

    @Column(name = "mensaje", columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @Column(name = "fechaCreacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "idUsuario", length = 15, nullable = false)
    private String idUsuario;
}
