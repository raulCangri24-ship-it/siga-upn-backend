package com.sga.backend.controller;

import com.sga.backend.dto.PagoRequest;
import com.sga.backend.dto.PagoResponse;
import com.sga.backend.dto.PlanPagoRequest;
import com.sga.backend.entity.PlanPago;
import com.sga.backend.security.JwtUtil;
import com.sga.backend.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:5173",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final JwtUtil jwtUtil;

    // HU-10: Extraer y validar que el token corresponda a un ADMIN
    private boolean esAdmin(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return false;
        try {
            String token = header.substring(7);
            return "ADMIN".equals(jwtUtil.getRolFromToken(token));
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping
    public ResponseEntity<?> listarTodos(HttpServletRequest request) {
        if (!esAdmin(request))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acceso denegado: no tienes permisos para ver los pagos");
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<PagoResponse>> porEstudiante(@PathVariable String idEstudiante) {
        return ResponseEntity.ok(pagoService.listarPagosPorEstudiante(idEstudiante));
    }

    // HU-10: Solo ADMIN puede registrar, anular y crear planes de pago
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody PagoRequest req, HttpServletRequest request) {
        if (!esAdmin(request))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acceso denegado: no tienes permisos para gestionar pagos");
        try {
            return ResponseEntity.ok(pagoService.registrarPago(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable String id, HttpServletRequest request) {
        if (!esAdmin(request))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acceso denegado: no tienes permisos para gestionar pagos");
        try {
            return ResponseEntity.ok(pagoService.anularPago(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/plan")
    public ResponseEntity<?> crearPlan(@RequestBody PlanPagoRequest req, HttpServletRequest request) {
        if (!esAdmin(request))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acceso denegado: no tienes permisos para gestionar pagos");
        try {
            PlanPago plan = pagoService.crearPlanPago(req);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
