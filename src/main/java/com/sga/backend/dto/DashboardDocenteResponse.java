package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardDocenteResponse {
    private Integer totalDocentes;
    private Integer docentesActivos;
    private Integer totalSecciones;
    private Integer actasFirmadas;
    private Integer actasBorrador;
    private Double promedioAsistencia;
    private Double cumplimientoActas;
    private List<SeccionPorDocente> seccionesPorDocente;

    @Data
    @AllArgsConstructor
    public static class SeccionPorDocente {
        private String nombreDocente;
        private Integer totalSecciones;
        private Integer actasFirmadas;
    }
}
