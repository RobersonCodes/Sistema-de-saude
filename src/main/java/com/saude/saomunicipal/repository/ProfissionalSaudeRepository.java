package com.saude.saomunicipal.repository;

import com.saude.saomunicipal.entity.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfissionalSaudeRepository extends
        JpaRepository<ProfissionalSaude, Long>,
        JpaSpecificationExecutor<ProfissionalSaude> {

    List<ProfissionalSaude> findByEspecialidadeContainingIgnoreCase(String especialidade);
}