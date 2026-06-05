package com.sga.backend.controller;

import com.sga.backend.dto.DashboardDocenteResponse;
import com.sga.backend.dto.DashboardEstudiantilResponse;
import com.sga.backend.dto.DashboardRectorResponse;
import com.sga.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/estudiantil")
    public ResponseEntity<DashboardEstudiantilResponse> getEstudiantil() {
        return ResponseEntity.ok(dashboardService.getEstudiantil());
    }

    @GetMapping("/docente")
    public ResponseEntity<DashboardDocenteResponse> getDocente() {
        return ResponseEntity.ok(dashboardService.getDocente());
    }

    @GetMapping("/rector")
    public ResponseEntity<DashboardRectorResponse> getRector() {
        return ResponseEntity.ok(dashboardService.getRector());
    }
}
