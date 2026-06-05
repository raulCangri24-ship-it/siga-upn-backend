package com.sga.backend.repository;

import com.sga.backend.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AulaRepository extends JpaRepository<Aula, String> {
}