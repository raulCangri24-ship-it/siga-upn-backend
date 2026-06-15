package com.sga.backend.controller;

import com.sga.backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:5173",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/mis-notificaciones/{idUsuario}")
    public ResponseEntity<?> listar(@PathVariable String idUsuario) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(idUsuario));
    }

    @GetMapping("/no-leidas/{idUsuario}")
    public ResponseEntity<?> contarNoLeidas(@PathVariable String idUsuario) {
        return ResponseEntity.ok(
            java.util.Map.of("count", notificacionService.contarNoLeidas(idUsuario)));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> marcarLeida(@PathVariable String id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/leer-todas/{idUsuario}")
    public ResponseEntity<?> marcarTodasLeidas(@PathVariable String idUsuario) {
        notificacionService.marcarTodasLeidas(idUsuario);
        return ResponseEntity.ok().build();
    }
}
