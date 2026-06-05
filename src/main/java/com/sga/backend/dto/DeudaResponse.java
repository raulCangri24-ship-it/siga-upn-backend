package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeudaResponse {
    private String idDeuda;
    private String monto;
    private String concepto;
    private String fechaVencimiento;
    private String estado;
    private String idEstudiante;
}
