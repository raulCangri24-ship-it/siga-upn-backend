package com.sga.backend.repository;

import com.sga.backend.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, String> {

    Optional<Nota> findByIdEstudianteAndIdEvaluacion(String idEstudiante, String idEvaluacion);

    List<Nota> findByIdEvaluacion(String idEvaluacion);
}
