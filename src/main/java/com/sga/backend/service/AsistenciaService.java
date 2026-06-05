package com.sga.backend.service;

import com.sga.backend.dto.AsistenciaRequest;
import com.sga.backend.dto.AsistenciaResponse;
import com.sga.backend.dto.MaterialRequest;
import com.sga.backend.dto.MaterialResponse;
import com.sga.backend.entity.Asistencia;
import com.sga.backend.entity.Material;
import com.sga.backend.repository.AsistenciaRepository;
import com.sga.backend.repository.MaterialRepository;
import com.sga.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Registrar asistencia masiva por sesión
    @Transactional
    public List<AsistenciaResponse> registrarAsistencia(AsistenciaRequest req) {
        LocalDate fecha = LocalDate.parse(req.getFecha());

        for (AsistenciaRequest.AsistenciaItem item : req.getRegistros()) {
            // Buscar si ya existe registro para ese día
            asistenciaRepository
                .findByIdEstudianteAndIdSeccionAndFecha(
                    item.getIdEstudiante(), req.getIdSeccion(), fecha)
                .ifPresentOrElse(
                    a -> {
                        a.setEstado(Asistencia.EstadoAsistencia.valueOf(item.getEstado()));
                        asistenciaRepository.save(a);
                    },
                    () -> {
                        Asistencia a = new Asistencia();
                        a.setIdAsistencia("ASI" + System.currentTimeMillis() % 100000+
                            item.getIdEstudiante().substring(3));
                        a.setFecha(fecha);
                        a.setModalidad(Asistencia.Modalidad.valueOf(req.getModalidad()));
                        a.setEstado(Asistencia.EstadoAsistencia.valueOf(item.getEstado()));
                        a.setIdEstudiante(item.getIdEstudiante());
                        a.setIdSeccion(req.getIdSeccion());
                        a.setIdDocente(req.getIdDocente());
                        asistenciaRepository.save(a);
                    }
                );
        }

        return asistenciaRepository
            .findByIdSeccionAndFecha(req.getIdSeccion(), fecha)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Listar asistencia por sección y fecha
    public List<AsistenciaResponse> listarPorSeccionYFecha(
            String idSeccion, String fecha) {
        LocalDate localDate = LocalDate.parse(fecha);
        return asistenciaRepository
            .findByIdSeccionAndFecha(idSeccion, localDate)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Listar asistencia de un estudiante en una sección
    public List<AsistenciaResponse> listarPorEstudiante(
            String idEstudiante, String idSeccion) {
        return asistenciaRepository
            .findByIdEstudianteAndIdSeccion(idEstudiante, idSeccion)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── MATERIALES ──────────────────────────────────────────────────────────

    public List<MaterialResponse> listarMateriales(String idSeccion) {
        return materialRepository.findByIdSeccion(idSeccion)
            .stream().map(this::toMaterialResponse).collect(Collectors.toList());
    }

    @Transactional
    public MaterialResponse publicarMaterial(MaterialRequest req) {
        Material m = new Material();
        m.setIdMaterial(req.getIdMaterial());
        m.setTitulo(req.getTitulo());
        m.setTipo(Material.TipoMaterial.valueOf(req.getTipo()));
        m.setUrl(req.getUrl());
        m.setFechaPublicacion(LocalDateTime.now());
        m.setIdSeccion(req.getIdSeccion());
        m.setIdDocente(req.getIdDocente());
        materialRepository.save(m);
        return toMaterialResponse(m);
    }

    @Transactional
    public void eliminarMaterial(String idMaterial) {
        materialRepository.deleteById(idMaterial);
    }

    private AsistenciaResponse toResponse(Asistencia a) {
        String nombre = usuarioRepository.findById(a.getIdEstudiante())
            .map(u -> u.getNombre() + " " + u.getApellido())
            .orElse("—");
        return new AsistenciaResponse(
            a.getIdAsistencia(),
            a.getFecha().toString(),
            a.getModalidad().name(),
            a.getEstado().name(),
            a.getIdEstudiante(),
            nombre,
            a.getIdSeccion()
        );
    }

    private MaterialResponse toMaterialResponse(Material m) {
        return new MaterialResponse(
            m.getIdMaterial(),
            m.getTitulo(),
            m.getTipo().name(),
            m.getUrl(),
            m.getFechaPublicacion() != null
                ? m.getFechaPublicacion().format(FMT) : "—",
            m.getIdSeccion(),
            m.getIdDocente()
        );
    }
}