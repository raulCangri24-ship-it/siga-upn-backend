package com.sga.backend.controller;

import com.sga.backend.entity.Aula;
import com.sga.backend.repository.AulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/aulas")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AulaController {

    private final AulaRepository aulaRepository;

    @GetMapping
    public List<Aula> listar() {
        return aulaRepository.findAll();
    }
}