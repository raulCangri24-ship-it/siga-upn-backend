package com.sga.backend.repository;

import com.sga.backend.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, String> {

    List<Notificacion> findByIdUsuarioOrderByFechaCreacionDesc(String idUsuario);

    long countByIdUsuarioAndLeida(String idUsuario, Boolean leida);
}
