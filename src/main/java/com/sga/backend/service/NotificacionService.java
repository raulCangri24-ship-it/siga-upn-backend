package com.sga.backend.service;

import com.sga.backend.entity.Notificacion;
import com.sga.backend.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional
    public void crear(String idUsuario, String tipo, String mensaje) {
        Notificacion n = new Notificacion();
        n.setIdNotificacion("NTF" + String.format("%017d", System.currentTimeMillis()));
        n.setIdUsuario(idUsuario);
        n.setTipo(tipo);
        n.setMensaje(mensaje);
        n.setLeida(false);
        n.setFechaCreacion(LocalDateTime.now());
        notificacionRepository.save(n);
    }

    public List<Map<String, Object>> listarPorUsuario(String idUsuario) {
        return notificacionRepository
            .findByIdUsuarioOrderByFechaCreacionDesc(idUsuario)
            .stream()
            .map(n -> Map.<String, Object>of(
                "idNotificacion", n.getIdNotificacion(),
                "tipo",           n.getTipo(),
                "mensaje",        n.getMensaje(),
                "leida",          n.getLeida(),
                "fecha",          n.getFechaCreacion().format(FMT)
            ))
            .collect(Collectors.toList());
    }

    public long contarNoLeidas(String idUsuario) {
        return notificacionRepository.countByIdUsuarioAndLeida(idUsuario, false);
    }

    @Transactional
    public void marcarLeida(String idNotificacion) {
        notificacionRepository.findById(idNotificacion).ifPresent(n -> {
            n.setLeida(true);
            notificacionRepository.save(n);
        });
    }

    @Transactional
    public void marcarTodasLeidas(String idUsuario) {
        notificacionRepository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario)
            .stream()
            .filter(n -> !n.getLeida())
            .forEach(n -> {
                n.setLeida(true);
                notificacionRepository.save(n);
            });
    }
}
