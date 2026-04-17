package com.saude.saomunicipal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mensagem formatada para chatbot ou WhatsApp")
public record MensagemChatbotResponseDTO(

        @Schema(description = "Mensagem pronta para exibição", example = "Horários disponíveis para 2026-04-15:\n1. 08:00\n2. 08:30")
        String mensagem

) {
}