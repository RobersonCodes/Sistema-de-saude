package com.saude.saomunicipal.integration.sus.repository;

import com.saude.saomunicipal.integration.sus.entity.IntegracaoLog;
import com.saude.saomunicipal.integration.sus.enums.StatusIntegracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegracaoLogRepository extends JpaRepository<IntegracaoLog, Long> {

    List<IntegracaoLog> findByStatusIntegracao(StatusIntegracao statusIntegracao);

    List<IntegracaoLog> findByEntidadeReferenciaAndReferenciaIdOrderByDataEventoDesc(
            String entidadeReferencia,
            Long referenciaId
    );
}