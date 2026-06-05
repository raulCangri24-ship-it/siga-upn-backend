package com.sga.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class AsistenciaRequest {
    private String idSeccion;
    private String idDocente;
    private String fecha;
    private String modalidad;
    private List<AsistenciaItem> registros;

    @Data
    public static class AsistenciaItem {
        private String idEstudiante;
        private String estado;
    }
}