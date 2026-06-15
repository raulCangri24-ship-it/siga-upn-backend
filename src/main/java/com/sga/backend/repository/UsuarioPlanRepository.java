package com.sga.backend.repository;

import com.sga.backend.entity.UsuarioPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioPlanRepository extends JpaRepository<UsuarioPlan, String> {

    List<UsuarioPlan> findByIdUsuario(String idUsuario);
}
