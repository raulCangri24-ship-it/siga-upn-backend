package com.sga.backend.controller;

import com.sga.backend.dto.UsuarioRequest;
import com.sga.backend.dto.UsuarioResponse;
import com.sga.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable String id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody UsuarioRequest req) {
        try {
            return ResponseEntity.ok(usuarioService.crear(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @PathVariable String id,
            @RequestBody UsuarioRequest req) {
        try {
            return ResponseEntity.ok(usuarioService.editar(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(
                usuarioService.cambiarEstado(id, body.get("estado")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/csv")
    public ResponseEntity<List<String>> cargarCsv(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(usuarioService.cargarCsv(file));
    }
}