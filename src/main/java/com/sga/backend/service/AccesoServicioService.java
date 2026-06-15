package com.sga.backend.service;

import com.sga.backend.dto.AccesoServicioResponse;
import com.sga.backend.entity.AccesoServicio;
import com.sga.backend.repository.AccesoServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccesoServicioService {

    private final AccesoServicioRepository accesoServicioRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional
    public List<AccesoServicioResponse> provisionarAccesos(String idMatricula, String idEstudiante) {
        long base = System.currentTimeMillis();
        List<AccesoServicio> nuevos = new ArrayList<>();
        int contador = 0;

        for (AccesoServicio.Servicio servicio : AccesoServicio.Servicio.values()) {
            // No duplicar si ya existe acceso para este estudiante y servicio
            if (!accesoServicioRepository.findByIdEstudianteAndServicio(idEstudiante, servicio).isEmpty()) {
                continue;
            }

            AccesoServicio a = new AccesoServicio();
            a.setIdAcceso("ACC" + String.format("%09d", (base + contador) % 1_000_000_000L));
            a.setIdEstudiante(idEstudiante);
            a.setIdMatricula(idMatricula);
            a.setServicio(servicio);
            a.setEstado(AccesoServicio.EstadoAcceso.PENDIENTE);
            a.setIntentos(0);
            accesoServicioRepository.save(a);
            nuevos.add(a);
            contador++;
        }

        // Activación simulada inmediata
        for (AccesoServicio a : nuevos) {
            a.setEstado(AccesoServicio.EstadoAcceso.ACTIVO);
            a.setFechaActivacion(LocalDateTime.now());
            accesoServicioRepository.save(a);
        }

        return accesoServicioRepository.findByIdEstudiante(idEstudiante)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AccesoServicioResponse activarAcceso(String idAcceso) {
        AccesoServicio a = accesoServicioRepository.findById(idAcceso)
            .orElseThrow(() -> new RuntimeException("Acceso no encontrado: " + idAcceso));
        a.setEstado(AccesoServicio.EstadoAcceso.ACTIVO);
        a.setFechaActivacion(LocalDateTime.now());
        a.setIntentos(a.getIntentos() + 1);
        accesoServicioRepository.save(a);
        return toResponse(a);
    }

    @Transactional
    public void suspenderAccesosPorRestriccion(String idEstudiante, String tipo) {
        // Solo SERVICIOS y TOTAL suspenden los accesos digitales
        if ("MATRICULA".equals(tipo)) return;

        accesoServicioRepository
            .findByIdEstudianteAndEstado(idEstudiante, AccesoServicio.EstadoAcceso.ACTIVO)
            .forEach(a -> {
                a.setEstado(AccesoServicio.EstadoAcceso.SUSPENDIDO);
                a.setFechaSuspension(LocalDateTime.now());
                accesoServicioRepository.save(a);
            });
    }

    @Transactional
    public void rehabilitarAccesos(String idEstudiante) {
        accesoServicioRepository
            .findByIdEstudianteAndEstado(idEstudiante, AccesoServicio.EstadoAcceso.SUSPENDIDO)
            .forEach(a -> {
                a.setEstado(AccesoServicio.EstadoAcceso.ACTIVO);
                a.setFechaActivacion(LocalDateTime.now());
                accesoServicioRepository.save(a);
            });
    }

    public List<AccesoServicioResponse> obtenerAccesosEstudiante(String idEstudiante) {
        return accesoServicioRepository.findByIdEstudiante(idEstudiante)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public int reintentarAccesosFallidos() {
        List<AccesoServicio> fallidos = accesoServicioRepository
            .findByEstadoAndIntentosLessThan(AccesoServicio.EstadoAcceso.PENDIENTE, 3);

        for (AccesoServicio a : fallidos) {
            a.setIntentos(a.getIntentos() + 1);
            a.setEstado(AccesoServicio.EstadoAcceso.ACTIVO);
            a.setFechaActivacion(LocalDateTime.now());
            accesoServicioRepository.save(a);
        }

        return fallidos.size();
    }

    // HU12-04: Timeout de 2 minutos + HU12-05: reintentos con trazabilidad
    public int sincronizarConTimeout() {
        try {
            return CompletableFuture.supplyAsync(this::ejecutarSincronizacion)
                .orTimeout(2, TimeUnit.MINUTES)
                .get();
        } catch (java.util.concurrent.ExecutionException e) {
            if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                throw new RuntimeException("Tiempo de sincronización excedido (2 min)");
            }
            throw new RuntimeException("Error en sincronización: " + e.getCause().getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sincronización interrumpida");
        }
    }

    private int ejecutarSincronizacion() {
        List<AccesoServicio> pendientes = accesoServicioRepository
            .findByEstadoAndIntentosLessThan(AccesoServicio.EstadoAcceso.PENDIENTE, 3);
        int activados = 0;
        for (AccesoServicio a : pendientes) {
            boolean exito = false;
            for (int i = 0; i < 3 && !exito; i++) {
                try {
                    a.setIntentos(a.getIntentos() + 1);
                    a.setEstado(AccesoServicio.EstadoAcceso.ACTIVO);
                    a.setFechaActivacion(LocalDateTime.now());
                    accesoServicioRepository.save(a);
                    exito = true;
                    activados++;
                } catch (Exception e) {
                    if (i < 2) {
                        try { Thread.sleep(5000L); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    } else {
                        a.setEstado(AccesoServicio.EstadoAcceso.SUSPENDIDO);
                        accesoServicioRepository.save(a);
                    }
                }
            }
        }
        return activados;
    }

    private AccesoServicioResponse toResponse(AccesoServicio a) {
        return new AccesoServicioResponse(
            a.getIdAcceso(),
            a.getIdEstudiante(),
            a.getIdMatricula(),
            a.getServicio().name(),
            a.getEstado().name(),
            a.getFechaActivacion() != null ? a.getFechaActivacion().format(FMT) : null,
            a.getIntentos()
        );
    }
}
