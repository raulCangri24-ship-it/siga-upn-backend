package com.sga.backend.repository;

import com.sga.backend.entity.RestriccionFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestriccionRepository extends JpaRepository<RestriccionFinanciera, String> {

    List<RestriccionFinanciera> findByIdEstudianteAndActiva(String idEstudiante, Boolean activa);

    List<RestriccionFinanciera> findByIdDeudaAndActiva(String idDeuda, Boolean activa);
}
