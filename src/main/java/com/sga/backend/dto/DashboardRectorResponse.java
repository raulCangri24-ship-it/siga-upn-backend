package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardRectorResponse {
    private String periodo;
    private Integer totalAforo;
    private Integer cuposInscritos;
    private Double ocupacion;
    private Double totalDeudas;
    private Double totalPagos;
    private Double deudaPendiente;
    private Double proyeccionIngresos;
    private Boolean proyeccionParcial;
    private List<DistribucionPago> distribucionPagos;

    @Data
    @AllArgsConstructor
    public static class DistribucionPago {
        private String estado;
        private Integer cantidad;
        private Double monto;
    }
}
