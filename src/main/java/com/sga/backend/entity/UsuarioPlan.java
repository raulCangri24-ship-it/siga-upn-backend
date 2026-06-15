package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarioplan")
public class UsuarioPlan {

    @Id
    @Column(name = "idUsuarioPlan", length = 15)
    private String idUsuarioPlan;

    @Column(name = "idUsuario", length = 15, nullable = false)
    private String idUsuario;

    @Column(name = "idPlan", length = 10, nullable = false)
    private String idPlan;
}
