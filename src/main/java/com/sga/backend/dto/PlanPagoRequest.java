package com.sga.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PlanPagoRequest {
    private String idPlan;
    private Integer numeroCuotas;
    private LocalDate fechaInicio;
    private String idDeuda;
}
