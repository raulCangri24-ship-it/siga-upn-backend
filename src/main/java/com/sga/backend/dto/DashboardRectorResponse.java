package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardRectorResponse {
    private Double totalIngresos;
    private Integer deudasVencidas;
    private Double montoDeudaTotal;
    private Integer restriccionesActivas;
    private Integer estudiantesConAccesoActivo;
    private Double ocupacionPromedio;
    private ResumenFinanciero resumenFinanciero;

    @Data
    @AllArgsConstructor
    public static class ResumenFinanciero {
        private Double totalCobrado;
        private Double totalPendiente;
        private Double totalVencido;
    }
}
