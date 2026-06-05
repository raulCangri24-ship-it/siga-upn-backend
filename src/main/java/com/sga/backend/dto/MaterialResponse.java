package com.sga.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class MaterialResponse {
    private String idMaterial;
    private String titulo;
    private String tipo;
    private String url;
    private String fechaPublicacion;
    private String idSeccion;
    private String idDocente;
}