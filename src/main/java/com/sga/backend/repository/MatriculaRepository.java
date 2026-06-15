package com.sga.backend.repository;

import com.sga.backend.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, String> {

    List<Matricula> findByIdEstudiante(String idEstudiante);

    List<Matricula> findByIdEstudianteAndIdPeriodo(String idEstudiante, String idPeriodo);

    Optional<Matricula> findByIdEstudianteAndIdSeccionAndIdPeriodo(
        String idEstudiante, String idSeccion, String idPeriodo);

    boolean existsByIdEstudianteAndIdSeccionAndIdPeriodo(
        String idEstudiante, String idSeccion, String idPeriodo);

    List<Matricula> findByIdSeccion(String idSeccion);

    List<Matricula> findByIdSeccionAndEstado(String idSeccion, Matricula.EstadoMatricula estado);

    boolean existsByIdEstudianteAndIdSeccionAndEstado(
        String idEstudiante, String idSeccion, Matricula.EstadoMatricula estado);
}