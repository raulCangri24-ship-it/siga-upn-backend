package com.sga.backend.repository;

import com.sga.backend.entity.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, String> {

    List<Deuda> findByIdEstudiante(String idEstudiante);

    List<Deuda> findByIdEstudianteAndEstado(String idEstudiante, Deuda.EstadoDeuda estado);

    List<Deuda> findByEstado(Deuda.EstadoDeuda estado);
}
