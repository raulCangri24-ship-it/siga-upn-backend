package com.sga.backend.controller;

import com.sga.backend.dto.PagoRequest;
import com.sga.backend.dto.PagoResponse;
import com.sga.backend.dto.PlanPagoRequest;
import com.sga.backend.entity.PlanPago;
import com.sga.backend.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:5173",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoResponse>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<PagoResponse>> porEstudiante(@PathVariable String idEstudiante) {
        return ResponseEntity.ok(pagoService.listarPagosPorEstudiante(idEstudiante));
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody PagoRequest req) {
        try {
            return ResponseEntity.ok(pagoService.registrarPago(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable String id) {
        try {
            return ResponseEntity.ok(pagoService.anularPago(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/plan")
    public ResponseEntity<?> crearPlan(@RequestBody PlanPagoRequest req) {
        try {
            PlanPago plan = pagoService.crearPlanPago(req);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
