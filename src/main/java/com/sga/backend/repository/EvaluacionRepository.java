package com.sga.backend.repository;

import com.sga.backend.entity.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, String> {

    List<Evaluacion> findByIdSilabo(String idSilabo);
}
