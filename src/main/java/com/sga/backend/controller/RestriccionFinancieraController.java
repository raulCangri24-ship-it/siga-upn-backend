package com.sga.backend.controller;

import com.sga.backend.service.SincronizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/restricciones")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class RestriccionFinancieraController {

    private final SincronizacionService sincronizacionService;

    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquear(@RequestBody Map<String, String> body) {
        try {
            String idEstudiante = body.get("idEstudiante");
            String idDeuda = body.get("idDeuda");
            return ResponseEntity.ok(sincronizacionService.activarRestriccion(idEstudiante, idDeuda, "TOTAL"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/desbloquear")
    public ResponseEntity<?> desbloquear(@RequestBody Map<String, String> body) {
        try {
            String idDeuda = body.get("idDeuda");
            return ResponseEntity.ok(sincronizacionService.levantarRestriccionPorDeuda(idDeuda));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
