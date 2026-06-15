package com.sga.backend.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class UsuarioPlanId implements Serializable {
    private String idUsuario;
    private String idPlan;
}