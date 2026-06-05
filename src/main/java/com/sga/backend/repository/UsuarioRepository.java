package com.sga.backend.repository;

import com.sga.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.intentosFallidos = u.intentosFallidos + 1 WHERE u.correo = :correo")
    void incrementarIntentosFallidos(String correo);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.intentosFallidos = 0 WHERE u.correo = :correo")
    void resetearIntentosFallidos(String correo);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.estado = 'BLOQUEADO' WHERE u.correo = :correo")
    void bloquearUsuario(String correo);
}
