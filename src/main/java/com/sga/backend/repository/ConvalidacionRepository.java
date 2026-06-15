package com.sga.backend.repository;

import com.sga.backend.entity.Convalidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvalidacionRepository extends JpaRepository<Convalidacion, String> {

    boolean existsByIdEstudianteAndIdCursoAndEstado(
        String idEstudiante, String idCurso, Convalidacion.EstadoConvalidacion estado);
}
