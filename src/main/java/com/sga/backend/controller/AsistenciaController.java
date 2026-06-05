package com.sga.backend.controller;

import com.sga.backend.dto.AsistenciaRequest;
import com.sga.backend.dto.AsistenciaResponse;
import com.sga.backend.dto.MaterialRequest;
import com.sga.backend.dto.MaterialResponse;
import com.sga.backend.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping("/asistencia")
    public ResponseEntity<?> registrar(@RequestBody AsistenciaRequest req) {
        try {
            return ResponseEntity.ok(asistenciaService.registrarAsistencia(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/asistencia/seccion/{idSeccion}/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponse>> porSeccionYFecha(
            @PathVariable String idSeccion,
            @PathVariable String fecha) {
        return ResponseEntity.ok(
            asistenciaService.listarPorSeccionYFecha(idSeccion, fecha));
    }

    @GetMapping("/asistencia/estudiante/{idEstudiante}/seccion/{idSeccion}")
    public ResponseEntity<List<AsistenciaResponse>> porEstudiante(
            @PathVariable String idEstudiante,
            @PathVariable String idSeccion) {
        return ResponseEntity.ok(
            asistenciaService.listarPorEstudiante(idEstudiante, idSeccion));
    }

    @GetMapping("/materiales/seccion/{idSeccion}")
    public ResponseEntity<List<MaterialResponse>> listarMateriales(
            @PathVariable String idSeccion) {
        return ResponseEntity.ok(
            asistenciaService.listarMateriales(idSeccion));
    }

    @PostMapping("/materiales")
    public ResponseEntity<?> publicarMaterial(@RequestBody MaterialRequest req) {
        try {
            return ResponseEntity.ok(asistenciaService.publicarMaterial(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/materiales/{id}")
    public ResponseEntity<?> eliminarMaterial(@PathVariable String id) {
        try {
            asistenciaService.eliminarMaterial(id);
            return ResponseEntity.ok("Material eliminado satisfactoriamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}