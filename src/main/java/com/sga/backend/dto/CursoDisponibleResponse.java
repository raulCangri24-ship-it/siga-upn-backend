package com.sga.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class CursoDisponibleResponse {
    private String idSeccion;
    private String idCurso;
    private String nombreCurso;
    private String codigo;
    private Integer creditos;
    private Integer ciclo;
    private String horario;
    private String aula;
    private Integer cuposDisponibles;
    private Integer capacidadMaxima;
    private Integer matriculados;
    private boolean prerrequisitoCumplido;
    private String mensajePrerrequisito;
    private boolean tieneRestriccion;
}