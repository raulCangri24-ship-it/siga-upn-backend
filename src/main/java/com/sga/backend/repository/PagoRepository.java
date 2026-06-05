package com.sga.backend.repository;

import com.sga.backend.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, String> {

    List<Pago> findByIdEstudiante(String idEstudiante);

    List<Pago> findByIdDeuda(String idDeuda);
}
