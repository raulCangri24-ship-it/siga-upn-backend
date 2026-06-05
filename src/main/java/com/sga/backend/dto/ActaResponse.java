package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActaResponse {
    private String idActa;
    private String idSeccion;
    private String idDocente;
    private String fechaGeneracion;
    private String estado;
    private String fechaFirma;
}
