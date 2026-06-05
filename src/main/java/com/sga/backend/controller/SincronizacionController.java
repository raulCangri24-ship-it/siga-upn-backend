package com.sga.backend.controller;

import com.sga.backend.dto.EstadoEstudianteResponse;
import com.sga.backend.service.SincronizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SincronizacionController {

    private final SincronizacionService sincronizacionService;

    @GetMapping("/estado/{idEstudiante}")
    public ResponseEntity<EstadoEstudianteResponse> getEstado(@PathVariable String idEstudiante) {
        return ResponseEntity.ok(sincronizacionService.obtenerEstadoCompleto(idEstudiante));
    }

    @PostMapping("/activar")
    public ResponseEntity<EstadoEstudianteResponse> activarRestriccion(@RequestBody Map<String, String> body) {
        String idEstudiante = body.get("idEstudiante");
        String idDeuda = body.get("idDeuda");
        String tipo = body.getOrDefault("tipo", "MATRICULA");
        return ResponseEntity.ok(sincronizacionService.activarRestriccion(idEstudiante, idDeuda, tipo));
    }

    @PostMapping("/levantar/{idRestriccion}")
    public ResponseEntity<EstadoEstudianteResponse> levantarRestriccion(@PathVariable String idRestriccion) {
        return ResponseEntity.ok(sincronizacionService.levantarRestriccion(idRestriccion));
    }

    @PostMapping("/ejecutar")
    public ResponseEntity<Map<String, Object>> ejecutarSincronizacion() {
        int count = sincronizacionService.sincronizarTodos();
        Map<String, Object> result = new HashMap<>();
        result.put("estudiantesSincronizados", count);
        result.put("mensaje", count > 0
            ? count + " estudiante(s) sincronizados con restricciones activas"
            : "Sin cambios — todos los estudiantes ya están sincronizados");
        return ResponseEntity.ok(result);
    }
}
