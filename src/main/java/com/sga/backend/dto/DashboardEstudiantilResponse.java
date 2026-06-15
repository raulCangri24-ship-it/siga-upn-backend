package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardEstudiantilResponse {
    private Integer totalEstudiantes;
    private Integer matriculadosActivos;
    private Integer deserciones;
    private Double tasaDesercion;
    private List<CarreraStats> porCarrera;
    private List<PeriodoStats> comparacionPeriodos;

    @Data
    @AllArgsConstructor
    public static class CarreraStats {
        private String carrera;
        private Integer matriculados;
        private Integer deserciones;
    }

    @Data
    @AllArgsConstructor
    public static class PeriodoStats {
        private String periodo;
        private Integer matriculados;
        private Integer deserciones;
        private Double tasaDesercion;
    }
}
