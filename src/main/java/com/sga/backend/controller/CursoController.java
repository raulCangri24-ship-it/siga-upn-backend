package com.sga.backend.controller;

import com.sga.backend.entity.Curso;
import com.sga.backend.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CursoController {

    private final CursoRepository cursoRepository;

    @GetMapping
    public List<Curso> listar() {
        return cursoRepository.findAll();
    }
}