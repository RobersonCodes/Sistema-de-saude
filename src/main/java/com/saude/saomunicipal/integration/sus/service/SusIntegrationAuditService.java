package com.saude.saomunicipal.integration.sus.service;

import com.saude.saomunicipal.integration.sus.entity.IntegracaoLog;
import com.saude.saomunicipal.integration.sus.enums.StatusIntegracao;
import com.saude.saomunicipal.integration.sus.enums.TipoIntegracaoSus;
import com.saude.saomunicipal.integration.sus.repository.IntegracaoLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SusIntegrationAuditService {

    private final IntegracaoLogRepository repository;

    public void registrarSucesso(
            TipoIntegracaoSus tipoIntegracao,
            String entidade,
            Long referenciaId,
            String endpoint,
            String payloadEnviado,
            String payloadResposta,
            String mensagem
    ) {
        repository.save(
                IntegracaoLog.builder()
                        .tipoIntegracao(tipoIntegracao)
                        .statusIntegracao(StatusIntegracao.ENVIADO)
                        .entidadeReferencia(entidade)
                        .referenciaId(referenciaId)
                        .endpointDestino(endpoint)
                        .payloadEnviado(payloadEnviado)
                        .payloadResposta(payloadResposta)
                        .mensagem(mensagem)
                        .sucesso(true)
                        .build()
        );
    }

    public void registrarErro(
            TipoIntegracaoSus tipoIntegracao,
            String entidade,
            Long referenciaId,
            String endpoint,
            String payloadEnviado,
            String payloadResposta,
            String mensagem
    ) {
        repository.save(
                IntegracaoLog.builder()
                        .tipoIntegracao(tipoIntegracao)
                        .statusIntegracao(StatusIntegracao.ERRO)
                        .entidadeReferencia(entidade)
                        .referenciaId(referenciaId)
                        .endpointDestino(endpoint)
                        .payloadEnviado(payloadEnviado)
                        .payloadResposta(payloadResposta)
                        .mensagem(mensagem)
                        .sucesso(false)
                        .build()
        );
    }
}