package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class EvaluacionDto {
    private String idEvaluacion;
    private String nombre;
    private String tipo;
    private BigDecimal peso;
}
