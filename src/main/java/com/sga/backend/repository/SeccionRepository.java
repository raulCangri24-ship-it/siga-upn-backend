package com.sga.backend.repository;

import com.sga.backend.entity.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, String> {

    List<Seccion> findByIdPeriodo(String idPeriodo);

    List<Seccion> findByIdDocente(String idDocente);

    @Query("SELECT s FROM Seccion s WHERE s.idDocente = :idDocente AND s.horario = :horario AND s.idPeriodo = :idPeriodo")
    List<Seccion> findCruceDocente(String idDocente, String horario, String idPeriodo);

    @Query("SELECT s FROM Seccion s WHERE s.idAula = :idAula AND s.horario = :horario AND s.idPeriodo = :idPeriodo")
    List<Seccion> findCruceAula(String idAula, String horario, String idPeriodo);

    List<Seccion> findByIdCurso(String idCurso);
}