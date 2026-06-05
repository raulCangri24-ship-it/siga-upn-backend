package com.sga.backend.service;

import com.sga.backend.dto.*;
import com.sga.backend.entity.*;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final SilaboRepository silaboRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final NotaRepository notaRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SeccionRepository seccionRepository;

    @Transactional
    public NotaResponse registrarNota(NotaRequest req) {
        if (req.getValor() == null ||
            req.getValor().compareTo(BigDecimal.ZERO) < 0 ||
            req.getValor().compareTo(new BigDecimal("20")) > 0) {
            throw new RuntimeException("La nota debe estar entre 0 y 20");
        }

        Optional<Nota> existing = notaRepository
            .findByIdEstudianteAndIdEvaluacion(req.getIdEstudiante(), req.getIdEvaluacion());

        Nota nota;
        if (existing.isPresent()) {
            nota = existing.get();
            nota.setValor(req.getValor());
            nota.setFechaRegistro(LocalDateTime.now());
        } else {
            nota = new Nota();
            nota.setIdNota(req.getIdNota() != null && !req.getIdNota().isBlank()
                ? req.getIdNota()
                : "NOT" + String.format("%09d", System.currentTimeMillis() % 1_000_000_000L));
            nota.setValor(req.getValor());
            nota.setFechaRegistro(LocalDateTime.now());
            nota.setIdEstudiante(req.getIdEstudiante());
            nota.setIdEvaluacion(req.getIdEvaluacion());
        }
        notaRepository.save(nota);

        Evaluacion eval = evaluacionRepository.findById(req.getIdEvaluacion()).orElse(null);
        return new NotaResponse(
            nota.getIdNota(),
            nota.getIdEvaluacion(),
            eval != null ? eval.getNombre() : "",
            eval != null ? eval.getTipo().name() : "",
            eval != null ? eval.getPeso() : BigDecimal.ZERO,
            nota.getValor()
        );
    }

    public PromedioResponse obtenerNotasEstudiante(String idEstudiante, String idSeccion) {
        Silabo silabo = silaboRepository.findByIdSeccion(idSeccion).orElse(null);
        Usuario usuario = usuarioRepository.findById(idEstudiante).orElse(null);
        String nombreEstudiante = usuario != null
            ? usuario.getNombre() + " " + usuario.getApellido()
            : idEstudiante;

        if (silabo == null) {
            return new PromedioResponse(idEstudiante, nombreEstudiante, idSeccion,
                new ArrayList<>(), BigDecimal.ZERO, "SIN SILABO");
        }

        List<Evaluacion> evaluaciones = evaluacionRepository.findByIdSilabo(silabo.getIdSilabo());
        List<NotaResponse> notas = new ArrayList<>();

        for (Evaluacion eval : evaluaciones) {
            Optional<Nota> notaOpt = notaRepository
                .findByIdEstudianteAndIdEvaluacion(idEstudiante, eval.getIdEvaluacion());
            notas.add(new NotaResponse(
                notaOpt.map(Nota::getIdNota).orElse(null),
                eval.getIdEvaluacion(),
                eval.getNombre(),
                eval.getTipo().name(),
                eval.getPeso(),
                notaOpt.map(Nota::getValor).orElse(null)
            ));
        }

        BigDecimal promedio = calcularPromedio(notas, evaluaciones);
        String estado = notas.stream().anyMatch(n -> n.getValor() != null)
            ? (promedio.compareTo(new BigDecimal("11")) >= 0 ? "APROBADO" : "DESAPROBADO")
            : "SIN NOTAS";

        return new PromedioResponse(idEstudiante, nombreEstudiante, idSeccion, notas, promedio, estado);
    }

    public List<PromedioResponse> obtenerNotasSeccion(String idSeccion) {
        return matriculaRepository.findByIdSeccion(idSeccion).stream()
            .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CONFIRMADA)
            .map(m -> obtenerNotasEstudiante(m.getIdEstudiante(), idSeccion))
            .collect(Collectors.toList());
    }

    public Optional<SilaboResponse> obtenerSilaboPorSeccion(String idSeccion) {
        return silaboRepository.findByIdSeccion(idSeccion).map(silabo -> {
            List<EvaluacionDto> evaluaciones = evaluacionRepository
                .findByIdSilabo(silabo.getIdSilabo()).stream()
                .map(e -> new EvaluacionDto(
                    e.getIdEvaluacion(), e.getNombre(), e.getTipo().name(), e.getPeso()))
                .collect(Collectors.toList());
            return new SilaboResponse(
                silabo.getIdSilabo(),
                silabo.getFormulaEvaluacion(),
                silabo.getIdSeccion(),
                silabo.getIdDocente(),
                silabo.getPublicado(),
                evaluaciones
            );
        });
    }

    public List<Seccion> obtenerSeccionesDocente(String idDocente) {
        return seccionRepository.findByIdDocente(idDocente);
    }

    // Usado por ActaService para calcular promedio sin depender de este servicio
    public BigDecimal calcularPromedioEstudiante(String idEstudiante, List<Evaluacion> evaluaciones) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalPeso = BigDecimal.ZERO;

        for (Evaluacion eval : evaluaciones) {
            Optional<Nota> notaOpt = notaRepository
                .findByIdEstudianteAndIdEvaluacion(idEstudiante, eval.getIdEvaluacion());
            if (notaOpt.isPresent() && eval.getPeso() != null) {
                total = total.add(
                    notaOpt.get().getValor().multiply(eval.getPeso().divide(new BigDecimal("100"))));
                totalPeso = totalPeso.add(eval.getPeso());
            }
        }

        if (totalPeso.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPromedio(List<NotaResponse> notas, List<Evaluacion> evaluaciones) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalPeso = BigDecimal.ZERO;

        for (int i = 0; i < notas.size(); i++) {
            NotaResponse n = notas.get(i);
            if (n.getValor() != null && n.getPeso() != null) {
                total = total.add(n.getValor().multiply(n.getPeso().divide(new BigDecimal("100"))));
                totalPeso = totalPeso.add(n.getPeso());
            }
        }

        if (totalPeso.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
