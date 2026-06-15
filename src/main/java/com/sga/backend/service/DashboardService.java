package com.sga.backend.service;

import com.sga.backend.dto.DashboardDocenteResponse;
import com.sga.backend.dto.DashboardEstudiantilResponse;
import com.sga.backend.dto.DashboardRectorResponse;
import com.sga.backend.entity.*;
import com.sga.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final AsistenciaRepository asistenciaRepository;
    private final AulaRepository aulaRepository;
    private final SilaboRepository silaboRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final CursoRepository cursoRepository;
    private final PlanEstudiosRepository planEstudiosRepository;
    private final PeriodoRepository periodoRepository;

    // HU-13: Dashboard Estudiantil
    public DashboardEstudiantilResponse getEstudiantil() {
        List<Usuario> todos = usuarioRepository.findAll();
        List<Usuario> estudiantes = todos.stream()
            .filter(u -> u.getRol() != null && "ROL002".equals(u.getRol().getIdRol()))
            .collect(Collectors.toList());
        int totalEstudiantes = estudiantes.size();

        Map<String, String> seccionToCurso = seccionRepository.findAll().stream()
            .collect(Collectors.toMap(Seccion::getIdSeccion, Seccion::getIdCurso, (a, b) -> a));
        Map<String, String> cursoToPlan = cursoRepository.findAll().stream()
            .collect(Collectors.toMap(Curso::getIdCurso, Curso::getIdPlan, (a, b) -> a));
        Map<String, String> planToCarrera = planEstudiosRepository.findAll().stream()
            .collect(Collectors.toMap(PlanEstudios::getIdPlan, PlanEstudios::getCarrera, (a, b) -> a));

        List<Matricula> todasMatriculas = matriculaRepository.findAll();
        List<Matricula> matriculasPer001 = todasMatriculas.stream()
            .filter(m -> "PER001".equals(m.getIdPeriodo()))
            .collect(Collectors.toList());

        int matriculadosActivos = (int) matriculasPer001.stream()
            .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CONFIRMADA).count();
        int deserciones = (int) matriculasPer001.stream()
            .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CANCELADA).count();
        double tasaDesercion = (matriculadosActivos + deserciones) > 0
            ? Math.round((deserciones * 1000.0 / (matriculadosActivos + deserciones))) / 10.0
            : 0.0;

        List<DashboardEstudiantilResponse.CarreraStats> porCarrera =
            calcularPorCarrera(matriculasPer001, seccionToCurso, cursoToPlan, planToCarrera);

        Map<String, PeriodoAcademico> periodosMap = periodoRepository.findAll().stream()
            .collect(Collectors.toMap(PeriodoAcademico::getIdPeriodo, p -> p));

        List<DashboardEstudiantilResponse.PeriodoStats> comparacion =
            List.of("PER001", "PER002", "PER003").stream()
                .map(pid -> {
                    List<Matricula> mats = todasMatriculas.stream()
                        .filter(m -> pid.equals(m.getIdPeriodo()))
                        .collect(Collectors.toList());
                    int conf = (int) mats.stream()
                        .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CONFIRMADA).count();
                    int canc = (int) mats.stream()
                        .filter(m -> m.getEstado() == Matricula.EstadoMatricula.CANCELADA).count();
                    double tasa = (conf + canc) > 0
                        ? Math.round((canc * 1000.0 / (conf + canc))) / 10.0 : 0.0;
                    String nombre = periodosMap.containsKey(pid)
                        ? periodosMap.get(pid).getNombre() : pid;
                    return new DashboardEstudiantilResponse.PeriodoStats(nombre, conf, canc, tasa);
                })
                .collect(Collectors.toList());

        return new DashboardEstudiantilResponse(
            totalEstudiantes, matriculadosActivos, deserciones, tasaDesercion,
            porCarrera, comparacion);
    }

    private List<DashboardEstudiantilResponse.CarreraStats> calcularPorCarrera(
            List<Matricula> matriculas,
            Map<String, String> seccionToCurso,
            Map<String, String> cursoToPlan,
            Map<String, String> planToCarrera) {

        Map<String, int[]> stats = new LinkedHashMap<>();
        for (Matricula m : matriculas) {
            String cursoId = seccionToCurso.get(m.getIdSeccion());
            String planId  = cursoId != null ? cursoToPlan.get(cursoId) : null;
            String carrera = planId != null ? planToCarrera.get(planId) : null;
            if (carrera == null) carrera = "Sin carrera";
            int[] s = stats.computeIfAbsent(carrera, k -> new int[]{0, 0});
            if (m.getEstado() == Matricula.EstadoMatricula.CONFIRMADA) s[0]++;
            else if (m.getEstado() == Matricula.EstadoMatricula.CANCELADA) s[1]++;
        }
        return stats.entrySet().stream()
            .map(e -> new DashboardEstudiantilResponse.CarreraStats(
                e.getKey(), e.getValue()[0], e.getValue()[1]))
            .collect(Collectors.toList());
    }

    // HU-14: Dashboard eficiencia docente
    public DashboardDocenteResponse getDocente(String periodo) {
        List<Usuario> todos = usuarioRepository.findAll();
        Map<String, Usuario> usuariosMap = todos.stream()
            .collect(Collectors.toMap(Usuario::getIdUsuario, u -> u));
        List<Usuario> docentes = todos.stream()
            .filter(u -> u.getRol() != null && "ROL003".equals(u.getRol().getIdRol()))
            .collect(Collectors.toList());

        List<Seccion> secciones = seccionRepository.findByIdPeriodo(periodo);
        Map<String, List<Seccion>> porDocente = secciones.stream()
            .collect(Collectors.groupingBy(Seccion::getIdDocente));

        List<DashboardDocenteResponse.DocenteStats> docentesList = docentes.stream()
            .map(doc -> {
                String idDoc = doc.getIdUsuario();
                String nombre = doc.getNombre() + " " + doc.getApellido();
                List<Seccion> secs = porDocente.getOrDefault(idDoc, Collections.emptyList());
                int seccionesAsignadas = secs.size();

                int seccionesConSilabo = (int) secs.stream()
                    .filter(s -> silaboRepository.findByIdSeccion(s.getIdSeccion()).isPresent())
                    .count();
                double cumplimiento = seccionesAsignadas > 0
                    ? round1((seccionesConSilabo * 100.0) / seccionesAsignadas) : 0.0;

                int estudiantesAsignados = secs.stream()
                    .mapToInt(s -> matriculaRepository
                        .findByIdSeccionAndEstado(s.getIdSeccion(), Matricula.EstadoMatricula.CONFIRMADA).size())
                    .sum();

                int asistenciasRegistradas = secs.stream()
                    .mapToInt(s -> asistenciaRepository.findByIdSeccion(s.getIdSeccion()).size())
                    .sum();

                int notasRegistradas = secs.stream()
                    .mapToInt(s -> {
                        Optional<Silabo> sil = silaboRepository.findByIdSeccion(s.getIdSeccion());
                        if (sil.isEmpty()) return 0;
                        return evaluacionRepository.findByIdSilabo(sil.get().getIdSilabo())
                            .stream()
                            .mapToInt(e -> notaRepository.findByIdEvaluacion(e.getIdEvaluacion()).size())
                            .sum();
                    })
                    .sum();

                double cargaEjecutada = estudiantesAsignados > 0
                    ? Math.min(round1((asistenciasRegistradas * 100.0) / (estudiantesAsignados * 16.0)), 100.0)
                    : 0.0;

                return new DashboardDocenteResponse.DocenteStats(
                    idDoc, nombre, seccionesAsignadas, seccionesConSilabo, cumplimiento,
                    estudiantesAsignados, asistenciasRegistradas, notasRegistradas, cargaEjecutada);
            })
            .collect(Collectors.toList());

        String nombrePeriodo = periodoRepository.findById(periodo)
            .map(PeriodoAcademico::getNombre).orElse(periodo);

        return new DashboardDocenteResponse(docentesList, nombrePeriodo);
    }

    // HU-15: Dashboard estratégico del rector
    public DashboardRectorResponse getRector(String periodo) {
        int totalAforo = aulaRepository.findAll().stream()
            .mapToInt(Aula::getAforo).sum();

        List<Seccion> seccionesPer = seccionRepository.findByIdPeriodo(periodo);
        int cuposInscritos = seccionesPer.stream()
            .mapToInt(s -> s.getMatriculados() != null ? s.getMatriculados() : 0).sum();

        double ocupacion = totalAforo > 0
            ? round2((cuposInscritos * 100.0) / totalAforo) : 0.0;

        List<Deuda> deudas = deudaRepository.findAll();
        double totalDeudas = deudas.stream()
            .mapToDouble(d -> d.getMonto().doubleValue()).sum();

        List<Pago> pagos = pagoRepository.findAll();
        double totalPagos = pagos.stream()
            .filter(p -> p.getEstado() == Pago.EstadoPago.CONFIRMADO)
            .mapToDouble(p -> p.getMonto().doubleValue()).sum();

        double deudaPendiente = totalDeudas - totalPagos;
        double proyeccionIngresos = totalPagos + (deudaPendiente * 0.7);

        boolean proyeccionParcial = deudas.stream()
            .anyMatch(d -> d.getEstado() == Deuda.EstadoDeuda.PENDIENTE
                || d.getEstado() == Deuda.EstadoDeuda.VENCIDA);

        Map<String, long[]> distMap = new LinkedHashMap<>();
        distMap.put("CONFIRMADO", new long[]{0, 0});
        distMap.put("PROCESANDO", new long[]{0, 0});
        distMap.put("ANULADO",    new long[]{0, 0});
        for (Pago p : pagos) {
            long[] arr = distMap.computeIfAbsent(p.getEstado().name(), k -> new long[]{0, 0});
            arr[0]++;
            arr[1] += Math.round(p.getMonto().doubleValue() * 100);
        }
        List<DashboardRectorResponse.DistribucionPago> distribucion = distMap.entrySet().stream()
            .filter(e -> e.getValue()[0] > 0)
            .map(e -> new DashboardRectorResponse.DistribucionPago(
                e.getKey(), (int) e.getValue()[0], round2(e.getValue()[1] / 100.0)))
            .collect(Collectors.toList());

        String nombrePeriodo = periodoRepository.findById(periodo)
            .map(PeriodoAcademico::getNombre).orElse(periodo);

        return new DashboardRectorResponse(
            nombrePeriodo, totalAforo, cuposInscritos, ocupacion,
            round2(totalDeudas), round2(totalPagos), round2(deudaPendiente),
            round2(proyeccionIngresos), proyeccionParcial, distribucion);
    }

    private double round1(double v) { return Math.round(v * 10.0) / 10.0; }
    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}
