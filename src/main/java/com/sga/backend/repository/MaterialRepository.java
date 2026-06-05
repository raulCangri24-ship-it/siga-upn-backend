package com.sga.backend.repository;

import com.sga.backend.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {
    List<Material> findByIdSeccion(String idSeccion);
    List<Material> findByIdDocente(String idDocente);
}