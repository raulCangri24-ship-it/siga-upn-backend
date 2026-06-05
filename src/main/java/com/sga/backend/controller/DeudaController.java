package com.sga.backend.controller;

import com.sga.backend.dto.DeudaRequest;
import com.sga.backend.dto.DeudaResponse;
import com.sga.backend.service.DeudaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deudas")
@CrossOrigin(origins = "http://localhost:5173",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
@RequiredArgsConstructor
public class DeudaController {

    private final DeudaService deudaService;

    @GetMapping
    public ResponseEntity<List<DeudaResponse>> listarTodas() {
        return ResponseEntity.ok(deudaService.listarTodas());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<DeudaResponse>> porEstudiante(@PathVariable String idEstudiante) {
        return ResponseEntity.ok(deudaService.listarPorEstudiante(idEstudiante));
    }

    @GetMapping("/estudiante/{idEstudiante}/verificar")
    public ResponseEntity<DeudaResponse> verificar(@PathVariable String idEstudiante) {
        Optional<DeudaResponse> restriccion = deudaService.verificarRestriccion(idEstudiante);
        return restriccion.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody DeudaRequest req) {
        try {
            return ResponseEntity.ok(deudaService.registrarDeuda(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/saldar")
    public ResponseEntity<?> saldar(@PathVariable String id) {
        try {
            return ResponseEntity.ok(deudaService.saldarDeuda(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
