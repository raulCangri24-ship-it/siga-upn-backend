package com.sga.backend.controller;

import com.sga.backend.dto.CursoDisponibleResponse;
import com.sga.backend.dto.MatriculaRequest;
import com.sga.backend.dto.MatriculaResponse;
import com.sga.backend.service.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "http://localhost:5173",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping("/disponibles/{idEstudiante}/{idPeriodo}")
    public ResponseEntity<List<CursoDisponibleResponse>> disponibles(
            @PathVariable String idEstudiante,
            @PathVariable String idPeriodo) {
        return ResponseEntity.ok(
            matriculaService.obtenerCursosDisponibles(idEstudiante, idPeriodo));
    }

    @PostMapping
    public ResponseEntity<?> inscribir(@RequestBody MatriculaRequest req) {
        try {
            return ResponseEntity.ok(matriculaService.inscribir(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable String id) {
        try {
            matriculaService.cancelar(id);
            return ResponseEntity.ok("Matrícula cancelada satisfactoriamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<MatriculaResponse>> porEstudiante(
            @PathVariable String idEstudiante) {
        return ResponseEntity.ok(
            matriculaService.listarPorEstudiante(idEstudiante));
    }
}