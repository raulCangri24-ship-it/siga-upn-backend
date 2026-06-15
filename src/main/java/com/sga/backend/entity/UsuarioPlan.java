package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "usuarioplan")
@IdClass(UsuarioPlanId.class)
public class UsuarioPlan {

    @Id
    @Column(name = "idUsuario", length = 15, nullable = false)
    private String idUsuario;

    @Id
    @Column(name = "idPlan", length = 10, nullable = false)
    private String idPlan;

    @Column(name = "fechaAsignacion")
    private LocalDate fechaAsignacion;
}