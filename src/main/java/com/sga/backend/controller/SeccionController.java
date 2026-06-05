package com.sga.backend.controller;

import com.sga.backend.dto.SeccionRequest;
import com.sga.backend.dto.SeccionResponse;
import com.sga.backend.service.SeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/secciones")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SeccionController {

    private final SeccionService seccionService;

    @GetMapping
    public ResponseEntity<List<SeccionResponse>> listar() {
        return ResponseEntity.ok(seccionService.listarTodas());
    }

    @GetMapping("/periodo/{idPeriodo}")
    public ResponseEntity<List<SeccionResponse>> listarPorPeriodo(
            @PathVariable String idPeriodo) {
        return ResponseEntity.ok(seccionService.listarPorPeriodo(idPeriodo));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody SeccionRequest req) {
        try {
            return ResponseEntity.ok(seccionService.crear(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @PathVariable String id,
            @RequestBody SeccionRequest req) {
        try {
            return ResponseEntity.ok(seccionService.editar(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            seccionService.eliminar(id);
            return ResponseEntity.ok("Sección eliminada satisfactoriamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}