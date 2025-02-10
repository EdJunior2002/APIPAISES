package com.meuprojeto.apipaises.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.meuprojeto.apipaises.model.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

    @Query("SELECT p FROM Pais p WHERE "
            + "(:regiao IS NULL OR p.regiao = :regiao) AND "
            + "(:populacaoMaiorQue IS NULL OR p.populacao > :populacaoMaiorQue) AND "
            + "(:populacaoMenorQue IS NULL OR p.populacao < :populacaoMenorQue)")
    Page<Pais> findByFiltros(String regiao, Long populacaoMaiorQue, Long populacaoMenorQue, Pageable pageable);
}