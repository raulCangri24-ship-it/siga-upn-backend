package com.sga.backend.repository;

import com.sga.backend.entity.Acta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActaRepository extends JpaRepository<Acta, String> {

    List<Acta> findByIdSeccion(String idSeccion);
}
