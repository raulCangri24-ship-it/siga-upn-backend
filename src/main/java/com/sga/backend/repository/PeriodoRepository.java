package com.sga.backend.repository;

import com.sga.backend.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoRepository extends JpaRepository<PeriodoAcademico, String> {
}