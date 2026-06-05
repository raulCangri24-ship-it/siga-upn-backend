package com.sga.backend.repository;

import com.sga.backend.entity.PlanPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanPagoRepository extends JpaRepository<PlanPago, String> {

    List<PlanPago> findByIdDeuda(String idDeuda);
}
