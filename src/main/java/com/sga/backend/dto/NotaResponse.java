package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class NotaResponse {
    private String idNota;
    private String idEvaluacion;
    private String nombreEvaluacion;
    private String tipo;
    private BigDecimal peso;
    private BigDecimal valor;
}
