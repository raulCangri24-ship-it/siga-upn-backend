package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardDocenteResponse {
    private List<DocenteStats> docentes;
    private String periodo;

    @Data
    @AllArgsConstructor
    public static class DocenteStats {
        private String idDocente;
        private String nombre;
        private Integer seccionesAsignadas;
        private Integer seccionesConSilabo;
        private Double cumplimientoCurricular;
        private Integer estudiantesAsignados;
        private Integer asistenciasRegistradas;
        private Integer notasRegistradas;
        private Double cargaEjecutada;
    }
}
