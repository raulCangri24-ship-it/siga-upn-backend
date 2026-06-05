package com.sga.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SeccionResponse {
    private String idSeccion;
    private String codigo;
    private Integer capacidadMaxima;
    private Integer matriculados;
    private String horario;
    private String modalidad;
    private String curso;
    private String docente;
    private String aula;
    private String periodo;
    private String estado;
}