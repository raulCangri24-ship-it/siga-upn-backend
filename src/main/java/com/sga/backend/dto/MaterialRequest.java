package com.sga.backend.dto;

import lombok.Data;

@Data
public class MaterialRequest {
    private String idMaterial;
    private String titulo;
    private String tipo;
    private String url;
    private String idSeccion;
    private String idDocente;
}