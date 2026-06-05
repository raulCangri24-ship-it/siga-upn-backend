package com.sga.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagoResponse {
    private String idPago;
    private String monto;
    private String concepto;
    private String fecha;
    private String estado;
    private String idEstudiante;
    private String nombreEstudiante;
    private String idDeuda;
    private String conceptoDeuda;
}
