package com.sga.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AsistenciaResponse {
    private String idAsistencia;
    private String fecha;
    private String modalidad;
    private String estado;
    private String idEstudiante;
    private String nombreEstudiante;
    private String idSeccion;
}