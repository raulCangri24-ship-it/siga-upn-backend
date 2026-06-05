package com.sga.backend.dto;

import lombok.Data;

@Data
public class MatriculaRequest {
    private String idMatricula;
    private String idEstudiante;
    private String idSeccion;
    private String idPeriodo;
}