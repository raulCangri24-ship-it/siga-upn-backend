package com.sga.backend.service;

import com.sga.backend.dto.EstadoEstudianteResponse;
import com.sga.backend.entity.Deuda;
import com.sga.backend.entity.Matricula;
import com.sga.backend.entity.RestriccionFinanciera;
import com.sga.backend.entity.Usuario;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SincronizacionService {

    private final DeudaRepository deudaRepository;
    private final RestriccionRepository restriccionRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    @Lazy
    @Autowired
    private AccesoServicioService accesoServicioService;

    public EstadoEstudianteResponse obtenerEstadoCompleto(String idEstudiante) {
        Usuario usuario = usuarioRepository.findById(idEstudiante).orElse(null);
        String nombre = usuario != null
            ? usuario.getNombre() + " " + usuario.getApellido()
            : idEstudiante;

        List<RestriccionFinanciera> restriccionesActivas =
            restriccionRepository.findByIdEstudianteAndActiva(idEstudiante, true);
        boolean tieneRestriccion = !restriccionesActivas.isEmpty();

        String tipoRestriccion = null;
        String deudaVencida = null;
        String montoDeuda = null;

        if (tieneRestriccion) {
            RestriccionFinanciera r = restriccionesActivas.get(0);
            tipoRestriccion = r.getTipo().name();
            Deuda deuda = deudaRepository.findById(r.getIdDeuda()).orElse(null);
            if (deuda != null) {
                deudaVencida = deuda.getConcepto();
                montoDeuda = deuda.getMonto().toPlainString();
            }
        }

        long count = matriculaRepository.findByIdEstudiante(idEstudiante).stream()
            .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CONFIRMADA)
            .count();

        return new EstadoEstudianteResponse(
            idEstudiante, nombre, tieneRestriccion, tipoRestriccion,
            deudaVencida, montoDeuda, (int) count, !tieneRestriccion
        );
    }

    @Transactional
    public EstadoEstudianteResponse verificarYSincronizar(String idEstudiante) {
        List<Deuda> deudasVencidas = deudaRepository.findByIdEstudianteAndEstado(
            idEstudiante, Deuda.EstadoDeuda.VENCIDA);

        for (Deuda deuda : deudasVencidas) {
            List<RestriccionFinanciera> existentes =
                restriccionRepository.findByIdDeudaAndActiva(deuda.getIdDeuda(), true);
            if (existentes.isEmpty()) {
                crearRestriccion(idEstudiante, deuda.getIdDeuda(), RestriccionFinanciera.TipoRestriccion.MATRICULA);
            }
        }

        return obtenerEstadoCompleto(idEstudiante);
    }

    @Transactional
    public EstadoEstudianteResponse activarRestriccion(String idEstudiante, String idDeuda, String tipo) {
        deudaRepository.findById(idDeuda)
            .orElseThrow(() -> new RuntimeException("Deuda no encontrada: " + idDeuda));

        List<RestriccionFinanciera> existentes =
            restriccionRepository.findByIdDeudaAndActiva(idDeuda, true);
        if (existentes.isEmpty()) {
            crearRestriccion(idEstudiante, idDeuda, RestriccionFinanciera.TipoRestriccion.valueOf(tipo));
            // HU11-04: Notificar bloqueo al estudiante
            Deuda deuda = deudaRepository.findById(idDeuda).orElse(null);
            String concepto = deuda != null ? deuda.getConcepto() : "deuda pendiente";
            notificacionService.crear(idEstudiante, "BLOQUEO",
                "Tu acceso ha sido bloqueado por deuda pendiente: " + concepto);
        }

        return obtenerEstadoCompleto(idEstudiante);
    }

    @Transactional
    public EstadoEstudianteResponse levantarRestriccion(String idRestriccion) {
        RestriccionFinanciera r = restriccionRepository.findById(idRestriccion)
            .orElseThrow(() -> new RuntimeException("Restricción no encontrada: " + idRestriccion));

        if (!r.getActiva()) {
            throw new RuntimeException("La restricción ya está levantada");
        }

        r.setActiva(false);
        r.setFechaLevantamiento(LocalDateTime.now());
        restriccionRepository.save(r);

        // HU11-04: Notificar desbloqueo al estudiante
        notificacionService.crear(r.getIdEstudiante(), "DESBLOQUEO",
            "Tu restricción financiera ha sido levantada. Ya puedes acceder a los servicios.");

        // Rehabilitar accesos a servicios externos si los hubiera suspendidos
        accesoServicioService.rehabilitarAccesos(r.getIdEstudiante());

        return obtenerEstadoCompleto(r.getIdEstudiante());
    }

    @Transactional
    public int sincronizarTodos() {
        List<Deuda> deudasVencidas = deudaRepository.findByEstado(Deuda.EstadoDeuda.VENCIDA);
        Set<String> estudiantesAfectados = new HashSet<>();
        long base = System.currentTimeMillis();
        int contador = 0;

        for (Deuda deuda : deudasVencidas) {
            List<RestriccionFinanciera> existentes =
                restriccionRepository.findByIdDeudaAndActiva(deuda.getIdDeuda(), true);
            if (existentes.isEmpty()) {
                RestriccionFinanciera r = new RestriccionFinanciera();
                r.setIdRestriccion("RST" + String.format("%09d", (base + contador) % 1_000_000_000L));
                r.setTipo(RestriccionFinanciera.TipoRestriccion.MATRICULA);
                r.setActiva(true);
                r.setFechaEmision(LocalDateTime.now());
                r.setFechaLevantamiento(null);
                r.setIdEstudiante(deuda.getIdEstudiante());
                r.setIdDeuda(deuda.getIdDeuda());
                restriccionRepository.save(r);
                estudiantesAfectados.add(deuda.getIdEstudiante());
                contador++;
            }
        }

        return estudiantesAfectados.size();
    }

    private void crearRestriccion(String idEstudiante, String idDeuda, RestriccionFinanciera.TipoRestriccion tipo) {
        RestriccionFinanciera r = new RestriccionFinanciera();
        r.setIdRestriccion("RST" + String.format("%09d", System.currentTimeMillis() % 1_000_000_000L));
        r.setTipo(tipo);
        r.setActiva(true);
        r.setFechaEmision(LocalDateTime.now());
        r.setFechaLevantamiento(null);
        r.setIdEstudiante(idEstudiante);
        r.setIdDeuda(idDeuda);
        restriccionRepository.save(r);
    }
}
