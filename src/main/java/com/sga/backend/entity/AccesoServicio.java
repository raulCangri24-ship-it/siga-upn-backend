package com.sga.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "acceso_servicio")
public class AccesoServicio {

    @Id
    @Column(name = "idAcceso", length = 15)
    private String idAcceso;

    @Column(name = "idEstudiante", length = 15, nullable = false)
    private String idEstudiante;

    @Column(name = "idMatricula", length = 15, nullable = false)
    private String idMatricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "servicio", nullable = false)
    private Servicio servicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAcceso estado = EstadoAcceso.PENDIENTE;

    @Column(name = "fechaActivacion")
    private LocalDateTime fechaActivacion;

    @Column(name = "fechaSuspension")
    private LocalDateTime fechaSuspension;

    @Column(name = "intentos", nullable = false)
    private Integer intentos = 0;

    public enum Servicio {
        AULA_VIRTUAL, BIBLIOTECA, VIDEOCONFERENCIA
    }

    public enum EstadoAcceso {
        ACTIVO, SUSPENDIDO, PENDIENTE
    }
}
