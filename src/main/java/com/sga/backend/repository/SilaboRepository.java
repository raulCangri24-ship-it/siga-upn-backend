package com.sga.backend.repository;

import com.sga.backend.entity.Silabo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SilaboRepository extends JpaRepository<Silabo, String> {

    Optional<Silabo> findByIdSeccion(String idSeccion);
}
