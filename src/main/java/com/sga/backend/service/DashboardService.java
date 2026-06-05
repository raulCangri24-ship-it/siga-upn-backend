package com.sga.backend.service;

import com.sga.backend.dto.DashboardDocenteResponse;
import com.sga.backend.dto.DashboardEstudiantilResponse;
import com.sga.backend.dto.DashboardRectorResponse;
import com.sga.backend.entity.*;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;
    private final NotaRepository notaRepository;
    private final DeudaRepository deudaRepository;
    private final PagoRepository pagoRepository;
    private final SeccionRepository seccionRepository;
    private final ActaRepository actaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final AccesoServicioRepository accesoServicioRepository;
    private final RestriccionRepository restriccionRepository;

    public DashboardEstudiantilResponse getEstudiantil() {
        List<Usuario> todos = usuarioRepository.findAll();
        Map<String, Usuario> usuariosMap = todos.stream()
            .collect(Collectors.toMap(Usuario::getIdUsuario, u -> u));

        List<Usuario> estudiantes = todos.stream()
            .filter(u -> u.getRol() != null && "ROL002".equals(u.getRol().getIdRol()))
            .collect(Collectors.toList());

        int totalEstudiantes = estudiantes.size();
        int estudiantesActivos = (int) estudiantes.stream()
            .filter(u -> u.getEstado() == Usuario.EstadoUsuario.ACTIVO)
            .count();

        List<Matricula> matriculas = matriculaRepository.findAll();
        int totalMatriculas = (int) matriculas.stream()
            .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CONFIRMADA)
            .count();

        int estudiantesConDeudaVencida = (int) deudaRepository.findAll().stream()
            .filter(d -> d.getEstado() == Deuda.EstadoDeuda.VENCIDA)
            .map(Deuda::getIdEstudiante)
            .distinct()
            .count();

        long total = matriculas.size();
        long canceladas = matriculas.stream()
            .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CANCELADA)
            .count();
        double tasaDesercion = total > 0
            ? Math.round((canceladas * 1000.0 / total)) / 10.0
            : 0.0;

        List<Nota> notas = notaRepository.findAll();
        OptionalDouble avg = notas.stream()
            .filter(n -> n.getValor() != null)
            .mapToDouble(n -> n.getValor().doubleValue())
            .average();
        double promedioGeneral = avg.isPresent()
            ? Math.round(avg.getAsDouble() * 100.0) / 100.0
            : 0.0;

        Map<String, Long> estudiantesPorEstado = estudiantes.stream()
            .collect(Collectors.groupingBy(u -> u.getEstado().name(), Collectors.counting()));
        for (String s : new String[]{"ACTIVO", "INACTIVO", "BLOQUEADO"})
            estudiantesPorEstado.putIfAbsent(s, 0L);

        Map<String, Long> matriculasPorEstado = matriculas.stream()
            .collect(Collectors.groupingBy(m -> m.getEstado().name(), Collectors.counting()));
        for (String s : new String[]{"CONFIRMADA", "CANCELADA", "PENDIENTE"})
            matriculasPorEstado.putIfAbsent(s, 0L);

        Map<String, List<BigDecimal>> notasPorEst = notas.stream()
            .filter(n -> n.getValor() != null)
            .collect(Collectors.groupingBy(
                Nota::getIdEstudiante,
                Collectors.mapping(Nota::getValor, Collectors.toList())
            ));

        List<DashboardEstudiantilResponse.EstudianteRendimiento> top5 = notasPorEst.entrySet().stream()
            .map(e -> {
                double prom = e.getValue().stream()
                    .mapToDouble(BigDecimal::doubleValue).average().orElse(0.0);
                prom = Math.round(prom * 100.0) / 100.0;
                Usuario u = usuariosMap.get(e.getKey());
                String nombre = u != null ? u.getNombre() + " " + u.getApellido() : e.getKey();
                return new DashboardEstudiantilResponse.EstudianteRendimiento(e.getKey(), nombre, prom);
            })
            .sorted(Comparator.comparingDouble(
                DashboardEstudiantilResponse.EstudianteRendimiento::getPromedio).reversed())
            .limit(5)
            .collect(Collectors.toList());

        return new DashboardEstudiantilResponse(
            totalEstudiantes, estudiantesActivos, totalMatriculas,
            estudiantesConDeudaVencida, tasaDesercion, promedioGeneral,
            estudiantesPorEstado, matriculasPorEstado, top5
        );
    }

    public DashboardDocenteResponse getDocente() {
        List<Usuario> todos = usuarioRepository.findAll();
        Map<String, Usuario> usuariosMap = todos.stream()
            .collect(Collectors.toMap(Usuario::getIdUsuario, u -> u));

        List<Usuario> docentes = todos.stream()
            .filter(u -> u.getRol() != null && "ROL003".equals(u.getRol().getIdRol()))
            .collect(Collectors.toList());

        int totalDocentes = docentes.size();
        int docentesActivos = (int) docentes.stream()
            .filter(u -> u.getEstado() == Usuario.EstadoUsuario.ACTIVO)
            .count();

        List<Seccion> secciones = seccionRepository.findByIdPeriodo("PER001");
        int totalSecciones = secciones.size();

        List<Acta> actas = actaRepository.findAll();
        int actasFirmadas = (int) actas.stream()
            .filter(a -> a.getEstado() == Acta.EstadoActa.FIRMADA)
            .count();
        int actasBorrador = (int) actas.stream()
            .filter(a -> a.getEstado() == Acta.EstadoActa.BORRADOR)
            .count();

        List<Asistencia> asistencias = asistenciaRepository.findAll();
        long totalAs = asistencias.size();
        long presentes = asistencias.stream()
            .filter(a -> a.getEstado() == Asistencia.EstadoAsistencia.PRESENTE)
            .count();
        double promedioAsistencia = totalAs > 0
            ? Math.round(presentes * 1000.0 / totalAs) / 10.0
            : 0.0;

        int totalActas = actas.size();
        double cumplimientoActas = totalActas > 0
            ? Math.round(actasFirmadas * 1000.0 / totalActas) / 10.0
            : 0.0;

        Map<String, List<Seccion>> porDocente = secciones.stream()
            .collect(Collectors.groupingBy(Seccion::getIdDocente));

        List<DashboardDocenteResponse.SeccionPorDocente> seccionesPorDocente = porDocente.entrySet().stream()
            .map(e -> {
                String idDoc = e.getKey();
                Usuario u = usuariosMap.get(idDoc);
                String nombre = u != null ? u.getNombre() + " " + u.getApellido() : idDoc;
                int totalSec = e.getValue().size();
                Set<String> idsSec = e.getValue().stream()
                    .map(Seccion::getIdSeccion).collect(Collectors.toSet());
                int firmadas = (int) actas.stream()
                    .filter(a -> a.getEstado() == Acta.EstadoActa.FIRMADA
                        && idsSec.contains(a.getIdSeccion()))
                    .count();
                return new DashboardDocenteResponse.SeccionPorDocente(nombre, totalSec, firmadas);
            })
            .sorted(Comparator.comparing(
                DashboardDocenteResponse.SeccionPorDocente::getTotalSecciones).reversed())
            .collect(Collectors.toList());

        return new DashboardDocenteResponse(
            totalDocentes, docentesActivos, totalSecciones,
            actasFirmadas, actasBorrador, promedioAsistencia,
            cumplimientoActas, seccionesPorDocente
        );
    }

    public DashboardRectorResponse getRector() {
        List<Pago> pagos = pagoRepository.findAll();
        double totalIngresos = pagos.stream()
            .filter(p -> p.getEstado() == Pago.EstadoPago.CONFIRMADO)
            .mapToDouble(p -> p.getMonto().doubleValue())
            .sum();

        List<Deuda> deudas = deudaRepository.findAll();
        long deudasVencidas = deudas.stream()
            .filter(d -> d.getEstado() == Deuda.EstadoDeuda.VENCIDA).count();
        double montoDeudaTotal = deudas.stream()
            .filter(d -> d.getEstado() == Deuda.EstadoDeuda.VENCIDA)
            .mapToDouble(d -> d.getMonto().doubleValue()).sum();

        int restriccionesActivas = (int) restriccionRepository.findAll().stream()
            .filter(r -> Boolean.TRUE.equals(r.getActiva())).count();

        int estudiantesConAccesoActivo = (int) accesoServicioRepository.findAll().stream()
            .filter(a -> a.getEstado() == AccesoServicio.EstadoAcceso.ACTIVO)
            .map(AccesoServicio::getIdEstudiante).distinct().count();

        List<Seccion> secciones = seccionRepository.findAll();
        OptionalDouble ocup = secciones.stream()
            .filter(s -> s.getCapacidadMaxima() != null && s.getCapacidadMaxima() > 0)
            .mapToDouble(s -> (s.getMatriculados() * 100.0) / s.getCapacidadMaxima())
            .average();
        double ocupacionPromedio = ocup.isPresent()
            ? Math.round(ocup.getAsDouble() * 10.0) / 10.0
            : 0.0;

        double totalPendiente = deudas.stream()
            .filter(d -> d.getEstado() == Deuda.EstadoDeuda.PENDIENTE)
            .mapToDouble(d -> d.getMonto().doubleValue()).sum();

        DashboardRectorResponse.ResumenFinanciero resumen =
            new DashboardRectorResponse.ResumenFinanciero(
                round2(totalIngresos),
                round2(totalPendiente),
                round2(montoDeudaTotal)
            );

        return new DashboardRectorResponse(
            round2(totalIngresos),
            (int) deudasVencidas,
            round2(montoDeudaTotal),
            restriccionesActivas,
            estudiantesConAccesoActivo,
            ocupacionPromedio,
            resumen
        );
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
