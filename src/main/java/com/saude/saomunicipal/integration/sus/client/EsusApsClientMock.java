package com.saude.saomunicipal.integration.sus.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saude.saomunicipal.integration.sus.dto.ConsultaSusDTO;
import com.saude.saomunicipal.integration.sus.dto.SusIntegrationResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EsusApsClientMock implements SusIntegrationClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SusIntegrationResponseDTO enviarConsulta(ConsultaSusDTO consulta) {
        try {
            String payload = objectMapper.writeValueAsString(consulta);

            return new SusIntegrationResponseDTO(
                    true,
                    "ESUS-MOCK-" + UUID.randomUUID(),
                    "Consulta enviada com sucesso para ambiente mock e-SUS APS em " + LocalDateTime.now(),
                    payload
            );
        } catch (JsonProcessingException e) {
            return new SusIntegrationResponseDTO(
                    false,
                    null,
                    "Erro ao serializar payload para integração mock.",
                    e.getMessage()
            );
        }
    }
}