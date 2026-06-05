package com.sga.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class MatriculaResponse {
    private String idMatricula;
    private String estado;
    private String curso;
    private String seccion;
    private String horario;
    private String aula;
    private String periodo;
    private String fechaMatricula;
    private String idSeccion;
}
