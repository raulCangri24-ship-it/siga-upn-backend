package com.sga.backend.controller;

import com.sga.backend.dto.ActaResponse;
import com.sga.backend.service.ActaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actas")
@CrossOrigin(origins = "http://localhost:5173",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
@RequiredArgsConstructor
public class ActaController {

    private final ActaService actaService;

    @GetMapping
    public ResponseEntity<List<ActaResponse>> listar() {
        return ResponseEntity.ok(actaService.listarActas());
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generar(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(
                actaService.generarActa(body.get("idSeccion"), body.get("idDocente")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrar(@PathVariable String id) {
        try {
            return ResponseEntity.ok(actaService.cerrarActa(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // HU08-04: Cierre autorizado por coordinador cuando docente no disponible
    @PutMapping("/{id}/cerrar-coordinador")
    public ResponseEntity<?> cerrarPorCoordinador(@PathVariable String id) {
        try {
            return ResponseEntity.ok(actaService.cerrarPorCoordinador(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
