package com.sga.backend.service;

import com.sga.backend.dto.PagoRequest;
import com.sga.backend.dto.PagoResponse;
import com.sga.backend.dto.PlanPagoRequest;
import com.sga.backend.entity.*;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PlanPagoRepository planPagoRepository;
    private final DeudaRepository deudaRepository;
    private final RestriccionRepository restriccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final DeudaService deudaService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional
    public PagoResponse registrarPago(PagoRequest req) {
        Pago pago = new Pago();
        pago.setIdPago(req.getIdPago() != null && !req.getIdPago().isBlank()
            ? req.getIdPago()
            : "PAG" + String.format("%05d", System.currentTimeMillis() % 100_000));
        pago.setMonto(req.getMonto());
        pago.setConcepto(req.getConcepto());
        pago.setFecha(LocalDateTime.now());
        pago.setEstado(Pago.EstadoPago.CONFIRMADO);
        pago.setIdEstudiante(req.getIdEstudiante());
        pago.setIdDeuda(req.getIdDeuda());
        pagoRepository.save(pago);

        // Si el monto del pago cubre la deuda, saldarla y levantar restricciones
        Deuda deuda = deudaRepository.findById(req.getIdDeuda()).orElse(null);
        if (deuda != null && deuda.getEstado() != Deuda.EstadoDeuda.PAGADA
            && req.getMonto().compareTo(deuda.getMonto()) >= 0) {
            deudaService.saldarDeuda(req.getIdDeuda());
        }

        return toResponse(pago);
    }

    @Transactional
    public PagoResponse anularPago(String idPago) {
        Pago pago = pagoRepository.findById(idPago)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + idPago));

        if (pago.getEstado() == Pago.EstadoPago.ANULADO) {
            throw new RuntimeException("El pago ya está anulado");
        }

        pago.setEstado(Pago.EstadoPago.ANULADO);
        pagoRepository.save(pago);

        // Reactivar deuda si no quedan otros pagos CONFIRMADO para esa deuda
        Deuda deuda = deudaRepository.findById(pago.getIdDeuda()).orElse(null);
        if (deuda != null && deuda.getEstado() == Deuda.EstadoDeuda.PAGADA) {
            boolean hayOtrosPagos = pagoRepository.findByIdDeuda(pago.getIdDeuda()).stream()
                .anyMatch(p -> p.getEstado() == Pago.EstadoPago.CONFIRMADO
                    && !p.getIdPago().equals(idPago));
            if (!hayOtrosPagos) {
                deuda.setEstado(Deuda.EstadoDeuda.PENDIENTE);
                deudaRepository.save(deuda);
            }
        }

        return toResponse(pago);
    }

    public List<PagoResponse> listarPagosPorEstudiante(String idEstudiante) {
        return pagoRepository.findByIdEstudiante(idEstudiante).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<PagoResponse> listarTodos() {
        return pagoRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public PlanPago crearPlanPago(PlanPagoRequest req) {
        PlanPago plan = new PlanPago();
        plan.setIdPlan(req.getIdPlan() != null && !req.getIdPlan().isBlank()
            ? req.getIdPlan()
            : "PLN" + String.format("%05d", System.currentTimeMillis() % 100_000));
        plan.setNumeroCuotas(req.getNumeroCuotas());
        plan.setEstado(PlanPago.EstadoPlan.VIGENTE);
        plan.setFechaInicio(req.getFechaInicio());
        plan.setIdDeuda(req.getIdDeuda());
        planPagoRepository.save(plan);

        // Suspender restricción activa mientras el plan esté vigente
        restriccionRepository.findByIdDeudaAndActiva(req.getIdDeuda(), true).forEach(r -> {
            r.setActiva(false);
            r.setFechaLevantamiento(LocalDateTime.now());
            restriccionRepository.save(r);
        });

        return plan;
    }

    private PagoResponse toResponse(Pago p) {
        Usuario usuario = usuarioRepository.findById(p.getIdEstudiante()).orElse(null);
        String nombreEstudiante = usuario != null
            ? usuario.getNombre() + " " + usuario.getApellido()
            : p.getIdEstudiante();

        Deuda deuda = deudaRepository.findById(p.getIdDeuda()).orElse(null);
        String conceptoDeuda = deuda != null ? deuda.getConcepto() : p.getIdDeuda();

        return new PagoResponse(
            p.getIdPago(),
            p.getMonto() != null ? p.getMonto().toPlainString() : "0.00",
            p.getConcepto(),
            p.getFecha() != null ? p.getFecha().format(FMT) : "",
            p.getEstado().name(),
            p.getIdEstudiante(),
            nombreEstudiante,
            p.getIdDeuda(),
            conceptoDeuda
        );
    }
}
