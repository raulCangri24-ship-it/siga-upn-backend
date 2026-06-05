package com.sga.backend.service;

import com.sga.backend.dto.ActaResponse;
import com.sga.backend.entity.*;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActaService {

    private final ActaRepository actaRepository;
    private final MatriculaRepository matriculaRepository;
    private final SilaboRepository silaboRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionService evaluacionService;
    private final HistorialRepository historialRepository;
    private final CursoRepository cursoRepository;
    private final SeccionRepository seccionRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<ActaResponse> listarActas() {
        return actaRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ActaResponse generarActa(String idSeccion, String idDocente) {
        Acta acta = new Acta();
        acta.setIdActa("ACT" + String.format("%09d", System.currentTimeMillis() % 1_000_000_000L));
        acta.setFechaGeneracion(LocalDateTime.now());
        acta.setEstado(Acta.EstadoActa.BORRADOR);
        acta.setFechaFirma(null);
        acta.setIdSeccion(idSeccion);
        acta.setIdDocente(idDocente);
        actaRepository.save(acta);
        return toResponse(acta);
    }

    @Transactional
    public ActaResponse cerrarActa(String idActa) {
        Acta acta = actaRepository.findById(idActa)
            .orElseThrow(() -> new RuntimeException("Acta no encontrada: " + idActa));

        if (acta.getEstado() == Acta.EstadoActa.FIRMADA) {
            throw new RuntimeException("El acta ya está firmada y no puede modificarse");
        }

        acta.setEstado(Acta.EstadoActa.FIRMADA);
        acta.setFechaFirma(LocalDateTime.now());
        actaRepository.save(acta);

        actualizarHistorial(acta.getIdSeccion());

        return toResponse(acta);
    }

    private void actualizarHistorial(String idSeccion) {
        Silabo silabo = silaboRepository.findByIdSeccion(idSeccion).orElse(null);
        if (silabo == null) return;

        List<Evaluacion> evaluaciones = evaluacionRepository.findByIdSilabo(silabo.getIdSilabo());

        Seccion seccion = seccionRepository.findById(idSeccion).orElse(null);
        int creditos = 0;
        if (seccion != null) {
            creditos = cursoRepository.findById(seccion.getIdCurso())
                .map(Curso::getCreditos).orElse(0);
        }

        List<Matricula> matriculas = matriculaRepository.findByIdSeccion(idSeccion).stream()
            .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CONFIRMADA)
            .collect(Collectors.toList());

        for (Matricula m : matriculas) {
            BigDecimal promedio = evaluacionService
                .calcularPromedioEstudiante(m.getIdEstudiante(), evaluaciones);

            final int creditosFinal = creditos;
            HistorialAcademico historial = historialRepository
                .findByIdEstudiante(m.getIdEstudiante())
                .orElseGet(() -> {
                    HistorialAcademico h = new HistorialAcademico();
                    h.setIdHistorial("HIS" +
                        String.format("%09d", System.currentTimeMillis() % 1_000_000_000L));
                    h.setIdEstudiante(m.getIdEstudiante());
                    h.setPromedioAcumulado(BigDecimal.ZERO);
                    h.setCreditosAprobados(0);
                    return h;
                });

            historial.setPromedioAcumulado(promedio);
            if (promedio.compareTo(new BigDecimal("11")) >= 0) {
                historial.setCreditosAprobados(historial.getCreditosAprobados() + creditosFinal);
            }
            historialRepository.save(historial);
        }
    }

    private ActaResponse toResponse(Acta a) {
        return new ActaResponse(
            a.getIdActa(),
            a.getIdSeccion(),
            a.getIdDocente(),
            a.getFechaGeneracion() != null ? a.getFechaGeneracion().format(FMT) : "",
            a.getEstado().name(),
            a.getFechaFirma() != null ? a.getFechaFirma().format(FMT) : null
        );
    }
}
