package com.sga.backend.service;

import com.sga.backend.dto.SeccionRequest;
import com.sga.backend.dto.SeccionResponse;
import com.sga.backend.entity.*;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeccionService {

    private final SeccionRepository seccionRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AulaRepository aulaRepository;
    private final PeriodoRepository periodoRepository;

    public List<SeccionResponse> listarPorPeriodo(String idPeriodo) {
        return seccionRepository.findByIdPeriodo(idPeriodo)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SeccionResponse> listarTodas() {
        return seccionRepository.findAll()
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SeccionResponse crear(SeccionRequest req) {

        // HU03-06: Validar que el periodo no esté cerrado
        PeriodoAcademico periodo = periodoRepository.findById(req.getIdPeriodo())
            .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));
        if (PeriodoAcademico.EstadoPeriodo.CERRADO.equals(periodo.getEstado())) {
            throw new RuntimeException(
                "No se puede programar clases: el periodo académico está cerrado");
        }

        // HU03-03: Validar cruce de horario del docente
        if (req.getIdDocente() != null && req.getHorario() != null) {
            List<Seccion> cruceDoc = seccionRepository.findCruceDocente(
                req.getIdDocente(), req.getHorario(), req.getIdPeriodo());
            if (!cruceDoc.isEmpty()) {
                throw new RuntimeException(
                    "Conflicto de horario: el docente ya tiene una clase asignada en ese horario");
            }
        }

        // HU03-04: Validar cruce de horario del aula
        if (req.getIdAula() != null && !req.getIdAula().isBlank() && req.getHorario() != null) {
            List<Seccion> cruceAula = seccionRepository.findCruceAula(
                req.getIdAula(), req.getHorario(), req.getIdPeriodo());
            if (!cruceAula.isEmpty()) {
                throw new RuntimeException(
                    "Conflicto de horario: el aula ya está ocupada en ese horario");
            }
        }

        // HU03-05: Validar aforo del aula vs capacidad solicitada
        if (req.getIdAula() != null && !req.getIdAula().isBlank()) {
            Aula aula = aulaRepository.findById(req.getIdAula())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));
            if (req.getCapacidadMaxima() > aula.getAforo()) {
                throw new RuntimeException(
                    "Aforo insuficiente: el aula '" + aula.getCodigo() +
                    "' tiene capacidad máxima de " + aula.getAforo() +
                    " personas, pero se solicitaron " + req.getCapacidadMaxima());
            }
        }

        cursoRepository.findById(req.getIdCurso())
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        usuarioRepository.findById(req.getIdDocente())
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        Seccion s = new Seccion();
        s.setIdSeccion(req.getIdSeccion());
        s.setCodigo(req.getCodigo());
        s.setCapacidadMaxima(req.getCapacidadMaxima());
        s.setMatriculados(0);
        s.setHorario(req.getHorario());
        s.setIdCurso(req.getIdCurso());
        s.setIdDocente(req.getIdDocente());
        s.setIdPeriodo(req.getIdPeriodo());

        if (req.getIdAula() != null && !req.getIdAula().isBlank()) {
            s.setIdAula(req.getIdAula());
        }

        seccionRepository.save(s);
        return toResponse(s);
    }

    public SeccionResponse editar(String id, SeccionRequest req) {
        Seccion s = seccionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

        // HU03-06: Validar que el periodo no esté cerrado antes de editar
        PeriodoAcademico periodo = periodoRepository.findById(s.getIdPeriodo())
            .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));
        if (PeriodoAcademico.EstadoPeriodo.CERRADO.equals(periodo.getEstado())) {
            throw new RuntimeException(
                "No se puede modificar la programación: el periodo académico está cerrado");
        }

        // HU03-03: Validar cruce de horario del docente (excluyendo la sección actual)
        if (req.getIdDocente() != null && req.getHorario() != null) {
            List<Seccion> cruceDoc = seccionRepository.findCruceDocente(
                req.getIdDocente(), req.getHorario(), s.getIdPeriodo());
            cruceDoc.removeIf(sec -> sec.getIdSeccion().equals(id));
            if (!cruceDoc.isEmpty()) {
                throw new RuntimeException(
                    "Conflicto de horario: el docente ya tiene una clase asignada en ese horario");
            }
        }

        // HU03-04: Validar cruce de horario del aula (excluyendo la sección actual)
        if (req.getIdAula() != null && !req.getIdAula().isBlank() && req.getHorario() != null) {
            List<Seccion> cruceAula = seccionRepository.findCruceAula(
                req.getIdAula(), req.getHorario(), s.getIdPeriodo());
            cruceAula.removeIf(sec -> sec.getIdSeccion().equals(id));
            if (!cruceAula.isEmpty()) {
                throw new RuntimeException(
                    "Conflicto de horario: el aula ya está ocupada en ese horario");
            }
        }

        // HU03-05: Validar aforo del aula vs capacidad solicitada
        if (req.getIdAula() != null && !req.getIdAula().isBlank()) {
            Aula aula = aulaRepository.findById(req.getIdAula())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));
            if (req.getCapacidadMaxima() > aula.getAforo()) {
                throw new RuntimeException(
                    "Aforo insuficiente: el aula '" + aula.getCodigo() +
                    "' tiene capacidad máxima de " + aula.getAforo() +
                    " personas, pero se solicitaron " + req.getCapacidadMaxima());
            }
        }

        s.setCodigo(req.getCodigo());
        s.setCapacidadMaxima(req.getCapacidadMaxima());
        s.setHorario(req.getHorario());
        s.setIdDocente(req.getIdDocente());
        s.setIdAula(req.getIdAula());

        seccionRepository.save(s);
        return toResponse(s);
    }

    public void eliminar(String id) {
        seccionRepository.deleteById(id);
    }

    private SeccionResponse toResponse(Seccion s) {
        String nombreCurso = cursoRepository.findById(s.getIdCurso())
            .map(Curso::getNombre).orElse("—");
        String nombreDocente = usuarioRepository.findById(s.getIdDocente())
            .map(u -> u.getNombre() + " " + u.getApellido()).orElse("—");
        String nombreAula = s.getIdAula() != null
            ? aulaRepository.findById(s.getIdAula()).map(Aula::getCodigo).orElse("—")
            : "Virtual";
        String nombrePeriodo = periodoRepository.findById(s.getIdPeriodo())
            .map(PeriodoAcademico::getNombre).orElse("—");

        String estado = s.getMatriculados() >= s.getCapacidadMaxima() ? "LLENO" : "DISPONIBLE";

        return new SeccionResponse(
            s.getIdSeccion(), s.getCodigo(), s.getCapacidadMaxima(),
            s.getMatriculados(), s.getHorario(), s.getIdDocente(),
            nombreCurso, nombreDocente, nombreAula, nombrePeriodo, estado
        );
    }
}