package com.sga.backend.repository;

import com.sga.backend.entity.HistorialAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HistorialRepository extends JpaRepository<HistorialAcademico, String> {
    Optional<HistorialAcademico> findByIdEstudiante(String idEstudiante);
}