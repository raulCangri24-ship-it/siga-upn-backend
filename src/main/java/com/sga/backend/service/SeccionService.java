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

        // Validar cruce de docente
        if (req.getIdDocente() != null && req.getHorario() != null) {
            List<Seccion> cruceDoc = seccionRepository.findCruceDocente(
                req.getIdDocente(), req.getHorario(), req.getIdPeriodo());
            if (!cruceDoc.isEmpty()) {
                throw new RuntimeException(
                    "Conflicto: el docente ya tiene clase en ese horario");
            }
        }

        // Validar cruce de aula
        if (req.getIdAula() != null && req.getHorario() != null) {
            List<Seccion> cruceAula = seccionRepository.findCruceAula(
                req.getIdAula(), req.getHorario(), req.getIdPeriodo());
            if (!cruceAula.isEmpty()) {
                throw new RuntimeException(
                    "Conflicto: el aula ya está ocupada en ese horario");
            }
        }

        cursoRepository.findById(req.getIdCurso())
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        usuarioRepository.findById(req.getIdDocente())
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        periodoRepository.findById(req.getIdPeriodo())
            .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));

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