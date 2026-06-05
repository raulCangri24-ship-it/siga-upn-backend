package com.sga.backend.repository;

import com.sga.backend.entity.Prerrequisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrerequisitoRepository extends JpaRepository<Prerrequisito, String> {
    List<Prerrequisito> findByIdCurso(String idCurso);
}