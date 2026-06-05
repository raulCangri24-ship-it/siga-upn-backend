package com.sga.backend.controller;

import com.sga.backend.entity.PeriodoAcademico;
import com.sga.backend.repository.PeriodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/periodos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PeriodoController {

    private final PeriodoRepository periodoRepository;

    @GetMapping
    public List<PeriodoAcademico> listar() {
        return periodoRepository.findAll();
    }
}