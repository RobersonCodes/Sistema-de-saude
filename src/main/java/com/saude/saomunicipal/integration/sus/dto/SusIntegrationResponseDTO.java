package com.saude.saomunicipal.integration.sus.dto;

public record SusIntegrationResponseDTO(
        boolean success,
        String protocolo,
        String mensagem,
        String rawResponse
) {
}