package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadoEstudianteResponse {
    private String idEstudiante;
    private String nombre;
    private Boolean tieneRestriccion;
    private String tipoRestriccion;
    private String deudaVencida;
    private String montoDeuda;
    private Integer matriculasActivas;
    private Boolean puedeMatricular;
}
