package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PromedioResponse {
    private String idEstudiante;
    private String nombreEstudiante;
    private String idSeccion;
    private List<NotaResponse> notas;
    private BigDecimal promedio;
    private String estado;
}
