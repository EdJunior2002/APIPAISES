package com.meuprojeto.apipaises.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.meuprojeto.apipaises.model.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {
}
