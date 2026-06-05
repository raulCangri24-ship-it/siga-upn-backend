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
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final SeccionRepository seccionRepository;
    private final CursoRepository cursoRepository;
    private final PrerequisitoRepository prerequisitoRepository;
    private final AulaRepository aulaRepository;

    @Lazy
    @Autowired
    private AccesoServicioService accesoServicioService;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<CursoDisponibleResponse> obtenerCursosDisponibles(
            String idEstudiante, String idPeriodo) {

        List<Seccion> secciones = seccionRepository.findByIdPeriodo(idPeriodo);
        List<CursoDisponibleResponse> resultado = new ArrayList<>();

        List<String> seccionesMatriculadas = matriculaRepository
            .findByIdEstudianteAndIdPeriodo(idEstudiante, idPeriodo)
            .stream()
            .filter(m -> m.getEstado() != Matricula.EstadoMatricula.CANCELADA)
            .map(Matricula::getIdSeccion)
            .collect(Collectors.toList());

        List<String> cursosAprobados = new ArrayList<>();

        for (Seccion s : secciones) {
            if (seccionesMatriculadas.contains(s.getIdSeccion())) continue;

            Curso curso = cursoRepository.findById(s.getIdCurso()).orElse(null);
            if (curso == null) continue;

            List<Prerrequisito> prereqs = prerequisitoRepository
                .findByIdCurso(s.getIdCurso());

            boolean prereqCumplido = true;
            String mensajePrereq = "Sin prerrequisitos";

            if (!prereqs.isEmpty()) {
                List<String> faltantes = new ArrayList<>();
                for (Prerrequisito p : prereqs) {
                    if (!cursosAprobados.contains(p.getIdCursoRequerido())) {
                        Curso cursoReq = cursoRepository
                            .findById(p.getIdCursoRequerido()).orElse(null);
                        if (cursoReq != null) {
                            faltantes.add(cursoReq.getNombre());
                        }
                    }
                }
                if (faltantes.isEmpty()) {
                    mensajePrereq = "Prerrequisitos cumplidos ✓";
                } else {
                    prereqCumplido = false;
                    mensajePrereq = "Falta: " + String.join(", ", faltantes);
                }
            }

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
            throw new RuntimeException("No hay cupos disponibles en esta sección");
        }

        List<String> cursosAprobados = new ArrayList<>();
        List<Prerrequisito> prereqs = prerequisitoRepository
            .findByIdCurso(seccion.getIdCurso());

        for (Prerrequisito p : prereqs) {
            if (!cursosAprobados.contains(p.getIdCursoRequerido())) {
                Curso cursoReq = cursoRepository
                    .findById(p.getIdCursoRequerido()).orElse(null);
                String nombre = cursoReq != null ? cursoReq.getNombre() : p.getIdCursoRequerido();
                throw new RuntimeException(
                    "No cumples el prerrequisito: " + nombre);
            }
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