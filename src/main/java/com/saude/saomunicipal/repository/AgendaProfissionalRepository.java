package com.saude.saomunicipal.repository;

import com.saude.saomunicipal.entity.AgendaProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendaProfissionalRepository extends JpaRepository<AgendaProfissional, Long> {

    List<AgendaProfissional> findByProfissionalIdAndData(Long profissionalId, LocalDate data);

    List<AgendaProfissional> findByProfissionalIdAndDataAndDisponivelTrue(Long profissionalId, LocalDate data);

    List<AgendaProfissional> findByUnidadeIdAndDataAndDisponivelTrue(Long unidadeId, LocalDate data);
}