package com.saude.saomunicipal.repository;

import com.saude.saomunicipal.entity.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UnidadeSaudeRepository extends
        JpaRepository<UnidadeSaude, Long>,
        JpaSpecificationExecutor<UnidadeSaude> {
}