package com.sga.backend.repository;

import com.sga.backend.entity.PlanEstudios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanEstudiosRepository extends JpaRepository<PlanEstudios, String> {
}
