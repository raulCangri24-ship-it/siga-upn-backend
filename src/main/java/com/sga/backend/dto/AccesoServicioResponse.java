package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccesoServicioResponse {
    private String idAcceso;
    private String idEstudiante;
    private String idMatricula;
    private String servicio;
    private String estado;
    private String fechaActivacion;
    private Integer intentos;
}
