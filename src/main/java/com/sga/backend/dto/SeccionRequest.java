package com.sga.backend.dto;

import lombok.Data;

@Data
public class SeccionRequest {
    private String idSeccion;
    private String codigo;
    private Integer capacidadMaxima;
    private String horario;
    private String modalidad;
    private String idCurso;
    private String idDocente;
    private String idAula;
    private String idPeriodo;
}