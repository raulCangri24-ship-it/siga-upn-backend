package com.sga.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoRequest {
    private String idPago;
    private BigDecimal monto;
    private String concepto;
    private String idEstudiante;
    private String idDeuda;
}
