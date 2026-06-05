package com.sga.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class NotaRequest {
    private String idNota;
    private BigDecimal valor;
    private String idEstudiante;
    private String idEvaluacion;
}
