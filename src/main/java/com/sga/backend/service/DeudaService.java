package com.sga.backend.service;

import com.sga.backend.dto.DeudaRequest;
import com.sga.backend.dto.DeudaResponse;
import com.sga.backend.entity.Deuda;
import com.sga.backend.entity.RestriccionFinanciera;
import com.sga.backend.repository.DeudaRepository;
import com.sga.backend.repository.RestriccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeudaService {

    private final DeudaRepository deudaRepository;
    private final RestriccionRepository restriccionRepository;

    @Lazy
    @Autowired
    private AccesoServicioService accesoServicioService;

    public List<DeudaResponse> listarTodas() {
        return deudaRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<DeudaResponse> listarPorEstudiante(String idEstudiante) {
        return deudaRepository.findByIdEstudiante(idEstudiante).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // Retorna la deuda asociada a la restricción activa, vacío si no tiene restricción
    public Optional<DeudaResponse> verificarRestriccion(String idEstudiante) {
        List<RestriccionFinanciera> restricciones =
            restriccionRepository.findByIdEstudianteAndActiva(idEstudiante, true);
        if (restricciones.isEmpty()) return Optional.empty();

        String idDeuda = restricciones.get(0).getIdDeuda();
        return deudaRepository.findById(idDeuda).map(this::toResponse);
    }

    @Transactional
    public DeudaResponse registrarDeuda(DeudaRequest req) {
        Deuda d = new Deuda();
        d.setIdDeuda(req.getIdDeuda());
        d.setMonto(req.getMonto());
        d.setConcepto(req.getConcepto());
        d.setFechaVencimiento(req.getFechaVencimiento());
        d.setEstado(req.getEstado() != null ? req.getEstado() : Deuda.EstadoDeuda.PENDIENTE);
        d.setIdEstudiante(req.getIdEstudiante());
        deudaRepository.save(d);

        if (d.getEstado() == Deuda.EstadoDeuda.VENCIDA) {
            crearRestriccion(d);
        }

        return toResponse(d);
    }

    @Transactional
    public DeudaResponse saldarDeuda(String idDeuda) {
        Deuda d = deudaRepository.findById(idDeuda)
            .orElseThrow(() -> new RuntimeException("Deuda no encontrada: " + idDeuda));

        d.setEstado(Deuda.EstadoDeuda.PAGADA);
        deudaRepository.save(d);

        restriccionRepository.findByIdDeudaAndActiva(idDeuda, true).forEach(r -> {
            r.setActiva(false);
            r.setFechaLevantamiento(LocalDateTime.now());
            restriccionRepository.save(r);
        });

        // Rehabilitar accesos a servicios externos al saldar la deuda
        accesoServicioService.rehabilitarAccesos(d.getIdEstudiante());

        return toResponse(d);
    }

    private void crearRestriccion(Deuda deuda) {
        RestriccionFinanciera r = new RestriccionFinanciera();
        r.setIdRestriccion("RST" + String.format("%09d", System.currentTimeMillis() % 1_000_000_000L));
        r.setTipo(RestriccionFinanciera.TipoRestriccion.MATRICULA);
        r.setActiva(true);
        r.setFechaEmision(LocalDateTime.now());
        r.setFechaLevantamiento(null);
        r.setIdEstudiante(deuda.getIdEstudiante());
        r.setIdDeuda(deuda.getIdDeuda());
        restriccionRepository.save(r);
    }

    private DeudaResponse toResponse(Deuda d) {
        return new DeudaResponse(
            d.getIdDeuda(),
            d.getMonto() != null ? d.getMonto().toPlainString() : "0.00",
            d.getConcepto(),
            d.getFechaVencimiento() != null ? d.getFechaVencimiento().toString() : "",
            d.getEstado().name(),
            d.getIdEstudiante()
        );
    }
}
