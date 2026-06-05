package com.sga.backend.repository;

import com.sga.backend.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, String> {
    List<Asistencia> findByIdSeccionAndFecha(String idSeccion, LocalDate fecha);
    List<Asistencia> findByIdSeccion(String idSeccion);
    List<Asistencia> findByIdEstudianteAndIdSeccion(String idEstudiante, String idSeccion);
    Optional<Asistencia> findByIdEstudianteAndIdSeccionAndFecha(
        String idEstudiante, String idSeccion, LocalDate fecha);
}