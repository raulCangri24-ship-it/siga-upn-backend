package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardEstudiantilResponse {
    private Integer totalEstudiantes;
    private Integer estudiantesActivos;
    private Integer totalMatriculas;
    private Integer estudiantesConDeudaVencida;
    private Double tasaDesercion;
    private Double promedioGeneral;
    private Map<String, Long> estudiantesPorEstado;
    private Map<String, Long> matriculasPorEstado;
    private List<EstudianteRendimiento> top5EstudiantesRendimiento;

    @Data
    @AllArgsConstructor
    public static class EstudianteRendimiento {
        private String idEstudiante;
        private String nombre;
        private Double promedio;
    }
}
