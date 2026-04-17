package com.saude.saomunicipal.integration.sus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saude.saomunicipal.entity.Consulta;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.integration.sus.client.SusIntegrationClient;
import com.saude.saomunicipal.integration.sus.dto.ConsultaSusDTO;
import com.saude.saomunicipal.integration.sus.dto.SusIntegrationResponseDTO;
import com.saude.saomunicipal.integration.sus.enums.TipoIntegracaoSus;
import com.saude.saomunicipal.integration.sus.mapper.SusIntegrationMapper;
import com.saude.saomunicipal.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SusIntegrationService {

    private final ConsultaRepository consultaRepository;
    private final SusIntegrationMapper mapper;
    private final SusIntegrationClient client;
    private final SusIntegrationAuditService auditService;
    private final ObjectMapper objectMapper;

    public SusIntegrationResponseDTO enviarConsultaParaEsusAps(Long consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new BusinessException("Consulta não encontrada para integração."));

        ConsultaSusDTO payload = mapper.toConsultaSusDTO(consulta);

        String payloadJson = toJson(payload);

        SusIntegrationResponseDTO response = client.enviarConsulta(payload);

        if (response.success()) {
            auditService.registrarSucesso(
                    TipoIntegracaoSus.ESUS_APS,
                    "CONSULTA",
                    consulta.getId(),
                    "mock://esus-aps/consultas",
                    payloadJson,
                    response.rawResponse(),
                    response.mensagem()
            );
        } else {
            auditService.registrarErro(
                    TipoIntegracaoSus.ESUS_APS,
                    "CONSULTA",
                    consulta.getId(),
                    "mock://esus-aps/consultas",
                    payloadJson,
                    response.rawResponse(),
                    response.mensagem()
            );
        }

        return response;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{\"erro\":\"Falha ao serializar payload\"}";
        }
    }
}