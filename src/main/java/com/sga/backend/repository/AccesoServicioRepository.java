package com.sga.backend.repository;

import com.sga.backend.entity.AccesoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccesoServicioRepository extends JpaRepository<AccesoServicio, String> {

    List<AccesoServicio> findByIdEstudiante(String idEstudiante);

    List<AccesoServicio> findByIdMatricula(String idMatricula);

    List<AccesoServicio> findByIdEstudianteAndServicio(String idEstudiante, AccesoServicio.Servicio servicio);

    List<AccesoServicio> findByIdEstudianteAndEstado(String idEstudiante, AccesoServicio.EstadoAcceso estado);

    List<AccesoServicio> findByEstadoAndIntentosLessThan(AccesoServicio.EstadoAcceso estado, int intentos);
}
