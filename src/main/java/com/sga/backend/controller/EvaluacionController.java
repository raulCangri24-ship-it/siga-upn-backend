package com.sga.backend.controller;

import com.sga.backend.dto.NotaRequest;
import com.sga.backend.dto.NotaResponse;
import com.sga.backend.dto.PromedioResponse;
import com.sga.backend.entity.Seccion;
import com.sga.backend.service.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    @PostMapping("/notas")
    public ResponseEntity<?> registrarNota(@RequestBody NotaRequest req) {
        try {
            NotaResponse resp = evaluacionService.registrarNota(req);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/notas/estudiante/{idEstudiante}/seccion/{idSeccion}")
    public ResponseEntity<PromedioResponse> notasEstudiante(
            @PathVariable String idEstudiante,
            @PathVariable String idSeccion) {
        return ResponseEntity.ok(
            evaluacionService.obtenerNotasEstudiante(idEstudiante, idSeccion));
    }

    @GetMapping("/notas/seccion/{idSeccion}")
    public ResponseEntity<List<PromedioResponse>> notasSeccion(
            @PathVariable String idSeccion) {
        return ResponseEntity.ok(evaluacionService.obtenerNotasSeccion(idSeccion));
    }

    @GetMapping("/silabo/seccion/{idSeccion}")
    public ResponseEntity<?> silaboPorSeccion(@PathVariable String idSeccion) {
        return evaluacionService.obtenerSilaboPorSeccion(idSeccion)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/notas/docente/{idDocente}/secciones")
    public ResponseEntity<List<Seccion>> seccionesDocente(@PathVariable String idDocente) {
        return ResponseEntity.ok(evaluacionService.obtenerSeccionesDocente(idDocente));
    }
}
