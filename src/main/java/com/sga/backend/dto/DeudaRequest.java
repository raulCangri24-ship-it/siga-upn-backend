package com.sga.backend.dto;

import com.sga.backend.entity.Deuda;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeudaRequest {
    private String idDeuda;
    private BigDecimal monto;
    private String concepto;
    private LocalDate fechaVencimiento;
    private Deuda.EstadoDeuda estado;
    private String idEstudiante;
}
