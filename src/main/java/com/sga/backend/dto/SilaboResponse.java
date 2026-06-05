package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SilaboResponse {
    private String idSilabo;
    private String formulaEvaluacion;
    private String idSeccion;
    private String idDocente;
    private Boolean publicado;
    private List<EvaluacionDto> evaluaciones;
}
