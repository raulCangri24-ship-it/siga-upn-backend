package com.sga.backend.service;

import com.sga.backend.dto.CursoDisponibleResponse;
import com.sga.backend.dto.MatriculaRequest;
import com.sga.backend.dto.MatriculaResponse;
import com.sga.backend.entity.*;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final SeccionRepository seccionRepository;
    private final CursoRepository cursoRepository;
    private final PrerequisitoRepository prerequisitoRepository;
    private final ConvalidacionRepository convalidacionRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final NotaRepository notaRepository;
    private final SilaboRepository silaboRepository;
    private final AulaRepository aulaRepository;
    private final UsuarioPlanRepository usuarioPlanRepository;

    @Lazy
    @Autowired
    private AccesoServicioService accesoServicioService;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // HU04-05: Obtener planes del estudiante
    private List<String> obtenerPlanes(String idEstudiante) {
        return usuarioPlanRepository.findByIdUsuario(idEstudiante)
            .stream().map(UsuarioPlan::getIdPlan).collect(Collectors.toList());
    }

    // HU04-01/02/03: Verificar si el estudiante aprobó un curso (promedio >= 11)
    private boolean verificarAprobacion(String idEstudiante, String idCurso) {
        List<Seccion> secciones = seccionRepository.findByIdCurso(idCurso);
        for (Seccion sec : secciones) {
            boolean tieneMatricula = matriculaRepository
                .existsByIdEstudianteAndIdSeccionAndEstado(
                    idEstudiante, sec.getIdSeccion(), Matricula.EstadoMatricula.CONFIRMADA);
            if (!tieneMatricula) continue;

            Optional<Silabo> silaboOpt = silaboRepository.findByIdSeccion(sec.getIdSeccion());
            if (silaboOpt.isEmpty()) continue;

            List<Evaluacion> evaluaciones = evaluacionRepository
                .findByIdSilabo(silaboOpt.get().getIdSilabo());
            if (evaluaciones.isEmpty()) continue;

            double pesosTotal = 0;
            double promedioPonderado = 0;
            for (Evaluacion eva : evaluaciones) {
                Optional<Nota> nota = notaRepository
                    .findByIdEstudianteAndIdEvaluacion(idEstudiante, eva.getIdEvaluacion());
                if (nota.isPresent() && eva.getPeso() != null) {
                    promedioPonderado += nota.get().getValor().doubleValue()
                        * eva.getPeso().doubleValue() / 100.0;
                    pesosTotal += eva.getPeso().doubleValue();
                }
            }
            if (pesosTotal > 0 && (promedioPonderado / (pesosTotal / 100.0)) >= 11.0) return true;
        }
        return false;
    }

    // HU04-01/02/03: Verificar prerrequisito (nota o convalidación)
    private String verificarPrerrequisitos(String idEstudiante, String idCurso) {
        List<Prerrequisito> prereqs = prerequisitoRepository.findByIdCurso(idCurso);
        List<String> faltantes = new ArrayList<>();

        for (Prerrequisito p : prereqs) {
            // HU04-03: Convalidación aprobada equivale a aprobar el curso
            boolean tieneConvalidacion = convalidacionRepository
                .existsByIdEstudianteAndIdCursoAndEstado(
                    idEstudiante, p.getIdCursoRequerido(),
                    Convalidacion.EstadoConvalidacion.APROBADA);
            if (tieneConvalidacion) continue;

            // HU04-01/02: Verificar aprobación por nota
            if (!verificarAprobacion(idEstudiante, p.getIdCursoRequerido())) {
                String nombre = cursoRepository.findById(p.getIdCursoRequerido())
                    .map(Curso::getNombre).orElse(p.getIdCursoRequerido());
                faltantes.add(nombre);
            }
        }

        return faltantes.isEmpty() ? null : "Falta: " + String.join(", ", faltantes);
    }

    public List<CursoDisponibleResponse> obtenerCursosDisponibles(
            String idEstudiante, String idPeriodo) {

        // HU04-05: Filtrar por plan de estudios del estudiante
        List<String> planesEstudiante = obtenerPlanes(idEstudiante);

        List<Seccion> secciones = seccionRepository.findByIdPeriodo(idPeriodo);
        List<CursoDisponibleResponse> resultado = new ArrayList<>();

        List<String> seccionesMatriculadas = matriculaRepository
            .findByIdEstudianteAndIdPeriodo(idEstudiante, idPeriodo)
            .stream()
            .filter(m -> m.getEstado() != Matricula.EstadoMatricula.CANCELADA)
            .map(Matricula::getIdSeccion)
            .collect(Collectors.toList());

        // HU04-05: Calcular ciclo máximo permitido para el estudiante
        int cicloMax = Integer.MAX_VALUE;
        List<UsuarioPlan> planesConFecha = usuarioPlanRepository.findByIdUsuario(idEstudiante);
        if (!planesConFecha.isEmpty() && planesConFecha.get(0).getFechaAsignacion() != null) {
            int anioIngreso = planesConFecha.get(0).getFechaAsignacion().getYear();
            int cicloActual = (2026 - anioIngreso) * 2 + 1;
            cicloMax = cicloActual + 1;
        }
        final int cicloMaxFinal = cicloMax;

        for (Seccion s : secciones) {
            if (seccionesMatriculadas.contains(s.getIdSeccion())) continue;

            Curso curso = cursoRepository.findById(s.getIdCurso()).orElse(null);
            if (curso == null) continue;

            // HU04-05: Solo cursos del plan del estudiante
            if (!planesEstudiante.isEmpty() && !planesEstudiante.contains(curso.getIdPlan())) continue;

            // HU04-05: Solo cursos hasta cicloActual+1
            if (curso.getCiclo() != null && curso.getCiclo() > cicloMaxFinal) continue;

            String mensajePrereq = verificarPrerrequisitos(idEstudiante, s.getIdCurso());
            boolean prereqCumplido = mensajePrereq == null;
            if (prereqCumplido) mensajePrereq = "Prerrequisitos cumplidos ✓";

            String nombreAula = s.getIdAula() != null
                ? aulaRepository.findById(s.getIdAula())
                    .map(Aula::getCodigo).orElse("Virtual")
                : "Virtual";

            int cuposDisponibles = s.getCapacidadMaxima() - s.getMatriculados();

            resultado.add(new CursoDisponibleResponse(
                s.getIdSeccion(),
                curso.getIdCurso(),
                curso.getNombre(),
                s.getCodigo(),
                curso.getCreditos(),
                curso.getCiclo(),
                s.getHorario(),
                nombreAula,
                cuposDisponibles,
                s.getCapacidadMaxima(),
                s.getMatriculados(),
                prereqCumplido,
                mensajePrereq,
                false
            ));
        }
        return resultado;
    }

    @Transactional
    public MatriculaResponse inscribir(MatriculaRequest req) {

        Optional<Matricula> matriculaExistente = matriculaRepository
            .findByIdEstudianteAndIdSeccionAndIdPeriodo(
                req.getIdEstudiante(), req.getIdSeccion(), req.getIdPeriodo());

        if (matriculaExistente.isPresent() &&
            matriculaExistente.get().getEstado() != Matricula.EstadoMatricula.CANCELADA) {
            throw new RuntimeException("Ya estás inscrito en esta sección");
        }

        Seccion seccion = seccionRepository.findById(req.getIdSeccion())
            .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

        if (seccion.getMatriculados() >= seccion.getCapacidadMaxima()) {
            throw new RuntimeException("No hay cupos disponibles en esta sección (LLENO)");
        }

        // HU04-01/02/03: Validar prerrequisitos con aprobación real y convalidaciones
        String errorPrereq = verificarPrerrequisitos(req.getIdEstudiante(), seccion.getIdCurso());
        if (errorPrereq != null) {
            throw new RuntimeException(
                "Prerrequisito pendiente: debes aprobar '" + errorPrereq.replace("Falta: ", "") +
                "' antes de matricularte en este curso");
        }

        Matricula m;
        if (matriculaExistente.isPresent()) {
            m = matriculaExistente.get();
            m.setEstado(Matricula.EstadoMatricula.CONFIRMADA);
            m.setFecha(java.time.LocalDateTime.now());
        } else {
            m = new Matricula();
            m.setIdMatricula(req.getIdMatricula());
            m.setIdEstudiante(req.getIdEstudiante());
            m.setIdSeccion(req.getIdSeccion());
            m.setIdPeriodo(req.getIdPeriodo());
            m.setEstado(Matricula.EstadoMatricula.CONFIRMADA);
        }
        matriculaRepository.save(m);

        try {
            accesoServicioService.provisionarAccesos(m.getIdMatricula(), req.getIdEstudiante());
        } catch (Exception ignored) {
        }

        seccion.setMatriculados(seccion.getMatriculados() + 1);
        seccionRepository.save(seccion);

        Curso curso = cursoRepository.findById(seccion.getIdCurso()).orElse(null);
        String nombreAula = seccion.getIdAula() != null
            ? aulaRepository.findById(seccion.getIdAula())
                .map(Aula::getCodigo).orElse("Virtual")
            : "Virtual";

        return new MatriculaResponse(
            m.getIdMatricula(),
            m.getEstado().name(),
            curso != null ? curso.getNombre() : "—",
            seccion.getCodigo(),
            seccion.getHorario(),
            nombreAula,
            req.getIdPeriodo(),
            m.getFecha().format(FMT),
            m.getIdSeccion()
        );
    }

    @Transactional
    public void cancelar(String idMatricula) {
        Matricula m = matriculaRepository.findById(idMatricula)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        m.setEstado(Matricula.EstadoMatricula.CANCELADA);
        matriculaRepository.save(m);

        Seccion s = seccionRepository.findById(m.getIdSeccion()).orElse(null);
        if (s != null && s.getMatriculados() > 0) {
            s.setMatriculados(s.getMatriculados() - 1);
            seccionRepository.save(s);
        }
    }

    public List<MatriculaResponse> listarPorEstudiante(String idEstudiante) {
        return matriculaRepository.findByIdEstudiante(idEstudiante)
            .stream()
            .map(m -> {
                Seccion s = seccionRepository.findById(m.getIdSeccion()).orElse(null);
                Curso c = s != null ? cursoRepository.findById(s.getIdCurso()).orElse(null) : null;
                String aula = s != null && s.getIdAula() != null
                    ? aulaRepository.findById(s.getIdAula()).map(Aula::getCodigo).orElse("Virtual")
                    : "Virtual";
                return new MatriculaResponse(
                    m.getIdMatricula(),
                    m.getEstado().name(),
                    c != null ? c.getNombre() : "—",
                    s != null ? s.getCodigo() : "—",
                    s != null ? s.getHorario() : "—",
                    aula,
                    m.getIdPeriodo(),
                    m.getFecha() != null ? m.getFecha().format(FMT) : "—",
                    m.getIdSeccion()
                );
            })
            .collect(Collectors.toList());
    }
}
