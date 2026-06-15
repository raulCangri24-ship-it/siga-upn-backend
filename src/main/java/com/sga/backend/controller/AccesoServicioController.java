package com.sga.backend.controller;

import com.sga.backend.dto.AccesoServicioResponse;
import com.sga.backend.service.AccesoServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accesos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AccesoServicioController {

    private final AccesoServicioService accesoServicioService;

    @GetMapping("/estudiante/{id}")
    public ResponseEntity<List<AccesoServicioResponse>> getAccesosEstudiante(@PathVariable String id) {
        return ResponseEntity.ok(accesoServicioService.obtenerAccesosEstudiante(id));
    }

    @PostMapping("/provisionar")
    public ResponseEntity<List<AccesoServicioResponse>> provisionar(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(accesoServicioService.provisionarAccesos(
            body.get("idMatricula"), body.get("idEstudiante")));
    }

    @PostMapping("/reintentar")
    public ResponseEntity<Map<String, Object>> reintentar() {
        int count = accesoServicioService.reintentarAccesosFallidos();
        Map<String, Object> result = new HashMap<>();
        result.put("accesosActivados", count);
        result.put("mensaje", count > 0
            ? count + " acceso(s) activado(s) exitosamente"
            : "No hay accesos pendientes de activación");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<Map<String, Object>> sincronizar() {
        try {
            int count = accesoServicioService.sincronizarConTimeout();
            Map<String, Object> result = new HashMap<>();
            result.put("accesosActivados", count);
            result.put("mensaje", count > 0
                ? count + " acceso(s) sincronizado(s) exitosamente"
                : "No hay accesos pendientes de sincronización");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    @PatchMapping("/suspender/{idEstudiante}")
    public ResponseEntity<Void> suspender(
            @PathVariable String idEstudiante,
            @RequestBody Map<String, String> body) {
        accesoServicioService.suspenderAccesosPorRestriccion(
            idEstudiante, body.getOrDefault("tipo", "TOTAL"));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/rehabilitar/{idEstudiante}")
    public ResponseEntity<Void> rehabilitar(@PathVariable String idEstudiante) {
        accesoServicioService.rehabilitarAccesos(idEstudiante);
        return ResponseEntity.ok().build();
    }
}
